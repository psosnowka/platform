package com.task.platform.application.api

import com.task.platform.domain.ProductNotFoundException
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class ExceptionHandler {

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException::class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    fun constraintViolationException(ex: ConstraintViolationException): ApiErrorsResponse {
        logger.info("ConstraintViolationException: $ex")

        return ApiErrorsResponse(
            errors = ex.constraintViolations.map { violation ->
                val propertyPath = violation.propertyPath ?: ""
                ApiErrorsResponse.ErrorResponse(
                    code = "invalid_request",
                    message = "Invalid value: ${violation.invalidValue}, $propertyPath ${violation.message}"
                )
            }
        ).logResponse()
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    fun methodArgumentNotValidException(ex: MethodArgumentNotValidException): ApiErrorsResponse {
        logger.info("MethodArgumentNotValidException: $ex")

        return ApiErrorsResponse(
            errors = ex.fieldErrors.map { error ->
                ApiErrorsResponse.ErrorResponse(
                    code = "invalid_request",
                    message = "Invalid value: ${error.rejectedValue},${error.defaultMessage}"
                )
            }
        ).logResponse()
    }


    @ResponseBody
    @ExceptionHandler(ProductNotFoundException::class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    fun productNotFound(e: ProductNotFoundException): ApiErrorsResponse {
        logger.info("ProductNotFoundException: ${e.message}")
        return ApiErrorsResponse.singleErrorResponse(
            code = "product_not_found",
            message = "Product not found for id: ${e.id.id}"
        )
    }

    @ExceptionHandler(Exception::class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    fun exception(e: Exception): ApiErrorsResponse {
        logger.info("Exception: $e")
        return ApiErrorsResponse.singleErrorResponse(
            code = "INTERNAL_SERVER_ERROR",
            message = "An unexpected error occurred"
        ).logResponse()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(com.task.platform.application.api.ExceptionHandler::class.java)
    }

    private fun ApiErrorsResponse.logResponse(): ApiErrorsResponse =
        also {
            logger.info("Error details: $this")
        }


}

data class ApiErrorsResponse(
    val errors: List<ErrorResponse>
) {

    data class ErrorResponse(
        val code: String,
        val message: String
    )

    companion object {
        fun singleErrorResponse(code: String, message: String): ApiErrorsResponse {
            return ApiErrorsResponse(
                listOf(
                    ErrorResponse(code, message)
                )
            )
        }
    }
}


