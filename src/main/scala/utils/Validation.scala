package utils

import cats.data.ValidatedNec
import cats.syntax.all._

import java.sql.Date
import java.time.{LocalDate, Period}

object Validation {

  sealed trait ValidationError

  object ValidationError {

    final case object InvalidVacationYear extends ValidationError {
      override def toString: String = "Vacation should be current year"
    }

    final case object PastDate extends ValidationError {
      override def toString: String = "Planning vacation goes for the future, not the past"
    }

    final case object InvalidPattern extends ValidationError {
      override def toString: String = "Date should match standard date pattern"
    }

    final case object InvalidLawVacationSize extends ValidationError {
      override def toString: String = "According to the law vacation should be at least 1 day and less than 25 days"
    }

  }

  import ValidationError._

  type AllErrorsOr[A] = ValidatedNec[ValidationError, A]

  private def validateDate(date: String): AllErrorsOr[Date] = {
    def validateDatePattern: AllErrorsOr[String] = {
      val aimPattern = "[0-9]{4}-(0[1-9]|1[012])-(0[1-9]|1[0-9]|2[0-9]|3[01])"
      val exceptedPattern = "(0[1-9]|1[0-9]|2[0-9]|3[01]).(0[1-9]|1[012]).[0-9]{4}"
      if (date.matches(aimPattern)) date.validNec
      else if (date.matches(exceptedPattern)) {
        val dateDate = exceptedPattern.r.findFirstIn(date).get.split("\\.").toList
        dateDate.mkString("-").validNec
      } else InvalidPattern.invalidNec
    }

    def validateYear: AllErrorsOr[Date] = {
      if (LocalDate.parse(date).getYear == LocalDate.now().getYear) Date.valueOf(date).validNec
      else InvalidVacationYear.invalidNec
    }

    def validatePast: AllErrorsOr[Date] = {
      if (Period.between(LocalDate.now(), LocalDate.parse(date)).getDays > 0) Date.valueOf(date).validNec
      else PastDate.invalidNec
    }

    validateDatePattern.productR(validatePast).productR(validateYear)
  }

  case class VacationBoundaries(startDate: Date, endDate: Date)

  def validateVacationSize(startDate: String, endDate: String): Boolean = {
    val difference = Period.between(LocalDate.parse(startDate), LocalDate.parse(endDate)).getDays
    0 < difference && difference < 25
  }

  def validate(startDate: String, endDate: String): AllErrorsOr[VacationBoundaries] =
    if (validateVacationSize(startDate, endDate)) (validateDate(startDate), validateDate(endDate)).mapN(VacationBoundaries)
    else InvalidLawVacationSize.invalidNec

}
