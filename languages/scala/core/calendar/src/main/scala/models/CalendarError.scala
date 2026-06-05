package models

enum CalendarError:
  case InvalidTime
  case PersonBusy(person: Person)
  case MeetingNotFound