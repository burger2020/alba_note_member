package com.albanote.memberservice.service.workplace

import com.albanote.memberservice.domain.dto.request.workplace.ChangeRepWorkplaceRequestDTO
import com.albanote.memberservice.domain.dto.request.workplace.CreateWorkplaceRequestDTO
import com.albanote.memberservice.domain.dto.request.workplace.CreateWorkplaceTodoRequestDTO
import com.albanote.memberservice.domain.dto.request.workplace.ModifyWorkplaceTodoRequestDTO
import com.albanote.memberservice.domain.dto.response.workplace.*
import com.albanote.memberservice.domain.entity.member.Member
import com.albanote.memberservice.domain.entity.workplace.EmployeeMember
import com.albanote.memberservice.domain.entity.workplace.EmployeeRank
import com.albanote.memberservice.domain.entity.workplace.Workplace
import com.albanote.memberservice.domain.entity.workplace.work.EmployeeTodo
import com.albanote.memberservice.domain.entity.workplace.work.Todo
import com.albanote.memberservice.repository.BossWorkplaceRepository
import com.albanote.memberservice.service.S3Service
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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
        val totalTodayTodoCount = workplaceRepository.findWorkplaceTodayTotalTodoCount(workplaceInfo.workplaceId)
        val currentEmployees = workplaceRepository.findWorkplaceCurrentEmployees(workplaceInfo.workplaceId)
        val requestList = getRequestList(workplaceInfo.workplaceId, PageRequest.of(0, 10))

        workplaceInfo.completedTodos.addAll(completedTodos)
        workplaceInfo.totalTodoCount = totalTodayTodoCount
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
    fun getRequestList(workplaceId: Long, pageable: Pageable): List<WorkplaceRequestSimpleResponseDTO> {
        val requestList = workplaceRepository.findRequestListByWorkplace(workplaceId, pageable)
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
        val employees = workplaceRepository.finEmployeesByWorkplace(workplaceId, currentEmployeeIds)
        //todo 직원 조회 방식 변경 필요 -> 직책 벙보 변경 또는 직원 직책 변경될 경우 어떻게 할 건지...

        currentEmployees.addAll(employees.map { WorkRecordResponseDTO(it, null, null) })

        currentEmployees.forEach {
            it.currentEmployee.imageUrl = s3service.convertCloudFrontUrl(it.currentEmployee.imageUrl)
        }

        return currentEmployees
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

        // 대표일터 설정안 되어 있으면 대표일터 설정
        val isExistRepWorkplace = workplaceRepository.findRepWorkplaceByMember(dto.bossMemberId) != null
        if (!isExistRepWorkplace)
            putChangeRepWorkplace(ChangeRepWorkplaceRequestDTO(workplace.id!!, dto.bossMemberId))

        return workplace.id
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
}
