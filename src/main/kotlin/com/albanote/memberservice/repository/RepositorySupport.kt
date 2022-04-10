package com.albanote.memberservice.repository

import com.querydsl.core.types.EntityPath
import com.querydsl.core.types.Expression
import com.querydsl.jpa.impl.JPADeleteClause
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import com.querydsl.jpa.impl.JPAUpdateClause
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.support.PageableExecutionUtils

open class RepositorySupport {
    @Autowired
    lateinit var queryFactory: JPAQueryFactory

    fun <T> select(expr: Expression<T>): JPAQuery<T> = queryFactory.select(expr)
    fun <T> selectFrom(path: EntityPath<T>): JPAQuery<T> = queryFactory.selectFrom(path)
    fun <T> delete(path: EntityPath<T>): JPADeleteClause = queryFactory.delete(path)
    fun <T> update(path: EntityPath<T>): JPAUpdateClause = queryFactory.update(path)

    fun <T> JPAQuery<T>.pageableOption(pageable: Pageable): JPAQuery<T> {
        this.offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
        return this
    }

    // content 와 count 쿼리가 같을경우 -> count 쿼리만 받고 pageOption 만 추가해서 페이지 번환
    fun <T> JPAQuery<T>.pageableCountUtilAndFetch(pageable: Pageable): Page<T> {
        val content = this.pageableOption(pageable).fetch()
        return PageableExecutionUtils.getPage(content, pageable) { this.fetchCount() }
    }

    // content 와 count 쿼리가 다를경우 -> count 쿼리와 content 쿼리 다 받고 pageOption 만 추가해서 페이지 번환
    fun <T, F> JPAQuery<T>.pageableCountUtilAndFetch(pageable: Pageable, func: () -> JPAQuery<F>): Page<T> {
        val content = this.pageableOption(pageable).fetch()
        return PageableExecutionUtils.getPage(content, pageable) { func.invoke().fetchCount()}
    }
}