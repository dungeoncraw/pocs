package models

import enums.ErrorCategory
import traits.ConversionError

case class GenericError(message: String, category: ErrorCategory) extends ConversionError
