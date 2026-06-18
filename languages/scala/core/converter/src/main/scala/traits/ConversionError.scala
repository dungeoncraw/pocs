package traits

import enums.ErrorCategory

trait ConversionError {
  def message: String
  def category: ErrorCategory
}
