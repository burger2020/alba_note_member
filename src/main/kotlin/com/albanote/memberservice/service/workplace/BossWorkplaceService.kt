package com.albanote.memberservice.service.workplace

import com.albanote.memberservice.domain.dto.request.workplace.*
import com.albanote.memberservice.domain.dto.response.workplace.*
import com.albanote.memberservice.domain.entity.member.Member
import com.albanote.memberservice.domain.entity.workplace.*
import com.albanote.memberservice.domain.entity.workplace.work.EmployeeTodo
import com.albanote.memberservice.domain.entity.workplace.work.Todo
import com.albanote.memberservice.domain.entity.workplace.work.TodoReferenceImage
import com.albanote.memberservice.domain.entity.workplace.work.WorkType
import com.albanote.memberservice.error.exception.workplace.NotFoundWorkRecordException
import com.albanote.memberservice.repository.workplace.BossWorkplaceRepository
import com.albanote.memberservice.service.S3Service
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDate
import javax.persistence.EntityManager

@Service
@Transactional(readOnly = true)
class BossWorkplaceService(
    private val workplaceRepository: BossWorkplaceRepository,
    private val s3service: S3Service,
    private val em: EntityManager
) {

    /** 홈화면 대표 일터 정보 조회 **/
    fun getWorkplaceInfo(memberId: Long, workplaceId: Long?): WorkplaceInfoOfBossResponseDTO {
        val workplaceInfo = workplaceRepository.findMyWorkplaceInfo(memberId, workplaceId) ?: throw Exception("없는 일터")
        workplaceInfo.workplaceImageUrl = s3service.convertCloudFrontUrl(workplaceInfo.workplaceImageUrl)
        val pageable = PageRequest.of(0, 10)
        val completedTodos =
            workplaceRepository.findWorkplaceTodoRecordsByDate(workplaceInfo.workplaceId, true, pageable)
        val currentEmployees = workplaceRepository.findWorkplaceCurrentEmployees(workplaceInfo.workplaceId)
        val requestList = getRequestList(workplaceInfo.workplaceId, PageRequest.of(0, 10), true)

        workplaceInfo.totalTodoCount = workplaceRepository.findWorkplaceTodayTotalTodoCount(workplaceInfo.workplaceId)
        workplaceInfo.totalEmployeeCount =
            workplaceRepository.findWorkplaceEmployeeTotalCount(workplaceInfo.workplaceId)
        workplaceInfo.completedTodos.addAll(completedTodos)
        workplaceInfo.currentEmployees.addAll(currentEmployees)
        workplaceInfo.workplaceRequest.addAll(requestList)

        workplaceInfo.completedTodos.forEach {
            it.completedMember?.imageUrl = s3service.convertCloudFrontUrl(it.completedMember?.imageUrl)
        }
        workplaceInfo.currentEmployees.forEach {
            it.currentEmployee.imageUrl = s3service.convertCloudFrontUrl(it.currentEmployee.imageUrl)
        }

        return workplaceInfo
    }

    /** 일터 목록 조회 **/
    fun getWorkplaceList(memberId: Long): List<WorkplaceListResponseDTO> {
        val workplaces = workplaceRepository.findWorkplaceListByMember(memberId)
        workplaces.forEach {
            it.workplaceImageUrl = s3service.convertCloudFrontUrl(it.workplaceImageUrl)
        }
        return workplaces
    }

    /** 요청 조회 **/
    fun getRequestList(
        workplaceId: Long,
        pageable: Pageable,
        isIncomplete: Boolean
    ): List<WorkplaceRequestSimpleResponseDTO> {
        val requestList = workplaceRepository.findRequestListByWorkplace(workplaceId, pageable, isIncomplete)
        requestList.forEach {
            it.requestMember.imageUrl = s3service.convertCloudFrontUrl(it.requestMember.imageUrl)
        }

        return requestList
    }

    /** 요청 상세 보기 **/
    fun getRequestDetail(requestId: Long): WorkplaceRequestDetailResponseDTO {
        val requestDetail = workplaceRepository.findRequestDetail(requestId) ?: throw Exception("존재하지 않는 요청")
        requestDetail.requestMember.imageUrl = s3service.convertCloudFrontUrl(requestDetail.requestMember.imageUrl)

        return requestDetail
    }

    /** 할 일 리스트 조회 **/
    fun getTodoRecordList(workplaceId: Long, pageable: Pageable): List<TodoRecordResponseDTO> {
        val todoRecords = workplaceRepository.findWorkplaceTodoRecordsByDate(workplaceId, false, pageable)
        todoRecords.forEach {
            it.completedMember?.imageUrl = s3service.convertCloudFrontUrl(it.completedMember?.imageUrl)
        }

        return todoRecords
    }

    /** 할 일 기록 상세 조회 **/
    fun getTodoRecordDetail(todoRecordId: Long?, todoId: Long): WorkplaceTodoRecordDetailResponseDTO {
        val todoRecordDetail = workplaceRepository.findWorkplaceTodoRecordDetail(todoRecordId, todoId)
        todoRecordDetail.todo?.chargeEmployee?.forEach {
            it.imageUrl = s3service.convertCloudFrontUrl(it.imageUrl)
        }

        return todoRecordDetail
    }

    /** 현재 근무 직원 + 그 외 직원 조회 **/
    fun getWorkRecordList(workplaceId: Long): MutableList<WorkRecordResponseDTO> {
        // 현재 근무중인 직원
        val currentEmployees = workplaceRepository.findWorkplaceCurrentEmployees(workplaceId)

        // 그 외 직원
        val currentEmployeeIds = currentEmployees.map { it.currentEmployee.memberId }
        val employees = workplaceRepository.findEmployeesByWorkplace(workplaceId, currentEmployeeIds)

        currentEmployees.addAll(employees.map { WorkRecordResponseDTO(it, null, null, WorkType.BEFORE_WORK) })

        currentEmployees.forEach {
            it.currentEmployee.imageUrl = s3service.convertCloudFrontUrl(it.currentEmployee.imageUrl)
        }

        return currentEmployees
    }

    /** 일별 근무 기록 조회**/
    fun getWorkRecordByDate(workplaceId: Long, date: LocalDate): MutableList<WorkRecordResponseDTO> {
        return if (date == LocalDate.now())
            getWorkRecordList(workplaceId)
        else {
            workplaceRepository.findWorkplaceCurrentEmployees(workplaceId)
        }
    }

    /** 근무 기록 상세 조회 **/
    fun getWorkRecordDetail(workRecordId: Long): WorkRecordDetailResponseDTO {
        val workRecordDetail = workplaceRepository.findWorkRecordDetail(workRecordId)
            ?: throw NotFoundWorkRecordException()

        workRecordDetail.employeeMember.imageUrl =
            s3service.convertCloudFrontUrl(workRecordDetail.employeeMember.imageUrl)

        return workRecordDetail
    }

    /** 직원별 일별 근무 기록 조회 **/
    fun getWorkRecordDetailByEmployee(employeeId: Long, date: LocalDate): WorkRecordDetailResponseDTO {
        val workRecordDetail = workplaceRepository.findWorkRecordDetailByEmployee(employeeId, date)
            ?: throw NotFoundWorkRecordException()

        workRecordDetail.employeeMember.imageUrl =
            s3service.convertCloudFrontUrl(workRecordDetail.employeeMember.imageUrl)

        return workRecordDetail
    }

    /** 일터 직원 간단한 정보 조회 **/
    fun getSimpleEmployeeList(workplaceId: Long): List<EmployeeMemberSimpleResponseDTO> {
        val employees = workplaceRepository.findSimpleEmployeeList(workplaceId)
        employees.forEach {
            it.imageUrl = s3service.convertCloudFrontUrl(it.imageUrl)
        }
        return employees
    }

    /************************ post **************************/

    /** 일터 생성 **/
    @Transactional
    fun postCreateWorkplace(dto: CreateWorkplaceRequestDTO): Long? {
        val workplace = dto.convertToEntity()
        em.persist(workplace)
        // 사장 직책 생성
        val bossEmployeeRank = EmployeeRank(workplace = workplace, name = dto.bossEmployeeRankName, isBoss = true)
        em.persist(bossEmployeeRank)
        // 사장 멤버 생성
        val bossEmployeeMember = EmployeeMember(
            member = Member(dto.bossMemberId),
            workplace = workplace,
            employeeRank = bossEmployeeRank,
            name = dto.bossEmployeeName,
            phoneNumber = dto.bossEmployeePhoneNumber
        )
        em.persist(bossEmployeeMember)
        // 일터 사진 정보 생성
        val workplaceImage = WorkplaceImage(workplace = workplace, imageUrl = null)
        em.persist(workplaceImage)

        // 대표일터 설정안 되어 있으면 대표일터 설정
        val isExistRepWorkplace = workplaceRepository.findRepWorkplaceByMember(dto.bossMemberId) != null
        if (!isExistRepWorkplace)
            putChangeRepWorkplace(ChangeRepWorkplaceRequestDTO(workplace.id!!, dto.bossMemberId))

        return workplace.id
    }

    /** 일터 사진 등록 **/
    @Transactional
    fun postCreateWorkplaceImage(workplaceId: Long, file: MultipartFile): String {
        val imageUrl = s3service.imageUpload(file, workplaceId, s3service.workplaceImage)
        val isExistImageUrl = workplaceRepository.checkExistWorkplaceImageUrl(workplaceId)
        if (!isExistImageUrl) {
            workplaceRepository.updateWorkplaceImage(workplaceId, imageUrl)
        }

        return imageUrl
    }

    /** 직책 생성 **/
    @Transactional
    fun postCreateEmployeeRank(dto: CreateEmployeeRankResponseDTO): Long? {
        val employeeRank = dto.convertToEntity()
        em.persist(employeeRank)
        dto.commuteTimeByDateOfWeek.forEach {
            em.persist(it.convertToEntity(employeeRank))
        }

        return employeeRank.id
    }

    /** 할일 생성 **/
    @Transactional
    fun postCreateTodo(dto: CreateWorkplaceTodoRequestDTO): Long? {
        val todo = Todo(
            createDate = LocalDate.now(),
            workplace = Workplace(dto.workplaceId),
            title = dto.todoTitle,
            description = dto.todoDescription,
            isAuthenticationImageRequire = dto.isAuthenticationImageRequire,
            startTime = dto.startTime,
            endTime = dto.endTime,
            todoCycleType = dto.todoCycleType,
            todoDays = dto.todoDays
        )
        val chargeEmployees = dto.chargeEmployeeIds.map {
            EmployeeTodo(
                employeeMember = EmployeeMember(it),
                todo = todo
            )
        }
        em.persist(todo)
        chargeEmployees.forEach {
            em.persist(it)
        }
        return todo.id
    }

    /** 할 일 참조 사진 등록 **/
    @Transactional
    fun postCreateTodoReferenceImage(todoId: Long, file: MultipartFile): String {
        val imageUrl = s3service.imageUpload(file, todoId, s3service.workplaceTodoImage)
        val todoImage = TodoReferenceImage(todo = Todo(todoId), imageUrl = imageUrl)
        em.persist(todoImage)

        return imageUrl
    }

    /************************ put **************************/

    /** 대표 일터 변경 **/
    @Transactional
    fun putChangeRepWorkplace(dto: ChangeRepWorkplaceRequestDTO): Boolean {
        return workplaceRepository.updateRepWorkplace(dto.memberId, dto.workplaceId)
    }

    /** 할 일 수정 **/
    @Transactional
    fun putModifyTodo(dto: ModifyWorkplaceTodoRequestDTO): Long? {
        workplaceRepository.deleteTodoToDeprecated(dto.todoIdToModify)

        return postCreateTodo(dto.convertToCreateDTO())
    }

    /** 직책 정보 변경 **/
    @Transactional
    fun putModifyEmployeeRank(dto: ModifyEmployeeRankResponseDTO): Long? {
        val modifiedRank = dto.convertToEntity()
        em.persist(modifiedRank)
        workplaceRepository.updateMembersEmployeeRank(modifiedRank, dto.rankId)
        val employeeIds = workplaceRepository.findEmployeeIdByRank(dto.rankId)
        val employeeMemberRanks = employeeIds.map {
            EmployeeMemberRank(
                createdDate = LocalDate.now(),
                employeeMember = EmployeeMember(),
                employeeRank = modifiedRank
            )
        }
        employeeMemberRanks.forEach {
            em.persist(it)
        }
        dto.commuteTimeByDateOfWeek.forEach {
            em.persist(it.convertToEntity(modifiedRank))
        }
        return modifiedRank.id
    }

    /****************** delete ****************/

    /** 일터 비활성화 **/
    @Transactional
    fun deleteDeleteWorkplace(memberId: Long, workplaceId: Long): Boolean {
        val isBoss = workplaceRepository.checkBossEmployeeByMemberAndWorkplace(memberId, workplaceId)
        if (!isBoss) {
            throw Exception("사장아닌 유저는 접근할 수 없습니다.")
        } else {
            return workplaceRepository.deleteWorkplaceToDeprecated(workplaceId)
        }
    }

    /** 할 일 삭제 **/
    @Transactional
    fun deleteDeleteTodo(todoId: Long): Boolean {
        return workplaceRepository.deleteTodoToDeprecated(todoId)
    }

    /** 직책 정보 삭제 **/
    @Transactional
    fun deleteDeleteEmployeeRank(rankId: Long): Boolean {
        //todo 직책에 등록된 직원 없어야 삭제가능하도록?
        return workplaceRepository.deleteEmployeeRankToDeprecated(rankId)
    }
}
