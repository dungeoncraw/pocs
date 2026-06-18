package traits

import traits.Converter

trait Parser[Raw, Parsed] extends Converter[Raw, Parsed] {
  def parse(raw: Raw): Either[List[Error], Parsed] = convert(raw)
}
