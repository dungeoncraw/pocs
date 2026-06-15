package socialmedia

import socialmedia.domain.*
import socialmedia.service.*
import socialmedia.view.*

@main
def main(): Unit =
  val program = for
    rian <- UserService.createUser("rian", "Rian Melon")
    bob <- UserService.createUser("bob", "Bob Uncle")
    zentopod <- UserService.createUser("zentopod", "Zentopod Cris")
    _ <- UserService.follow(bob.id, rian.id)
    _ <- UserService.follow(bob.id, zentopod.id)
    rianPhoto <- PhotoService.publishPhoto(rian.id, "https://example.com/rian-photo.jpg", "My first photo!")
    _ <- PhotoService.tagPhoto(rianPhoto.id, rian.id, bob.id)
    zentopodPhoto <- PhotoService.publishPhoto(zentopod.id, "https://example.com/zentopod-photo.jpg", "Beautiful sunset")
    _ <- PhotoService.commentOnPhoto(rianPhoto.id, bob.id, "Nice picture!")
    _ <- PhotoService.commentOnPhoto(zentopodPhoto.id, bob.id, "Amazing view!")
    timeline <- TimelineService.timelineFor(bob.id)
  yield timeline

  program.run(AppState.empty) match
    case Left(error) => println(s"Error: ${error.message}")
    case Right((_, timeline)) =>
      println(View.renderTimeline(timeline))
