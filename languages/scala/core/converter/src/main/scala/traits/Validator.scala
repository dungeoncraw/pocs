package traits

import traits.Converter

trait Validator[Parsed, Validated] extends Converter[Parsed, Validated] {
  def validate(parsed: Parsed): Either[List[Error], Validated] = convert(parsed)
}
