package socialmedia.view

import socialmedia.domain.*

object View:

  def renderTimeline(posts: List[TimelinePost]): String =
    posts.map(renderPost).mkString("\n")

  private def renderPost(post: TimelinePost): String =
    val tagged =
      if post.tags.isEmpty then "No tagged users"
      else post.tags.map(user => s"@${user.username}").mkString(", ")

    val comments =
      if post.comments.isEmpty then "No comments"
      else post.comments.map(comment => s"- ${comment.text}").mkString("\n")

    s"""
       |Photo by @${post.author.username}
       |Caption: ${post.photo.caption}
       |URL: ${post.photo.url}
       |Tagged: $tagged
       |Comments:
       |$comments
       |-----------------------------
       |""".stripMargin
