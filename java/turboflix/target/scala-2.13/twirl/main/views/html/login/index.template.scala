
package views.html.login

import _root_.play.twirl.api.TwirlFeatureImports._
import _root_.play.twirl.api.TwirlHelperImports._
import _root_.play.twirl.api.Html
import _root_.play.twirl.api.JavaScript
import _root_.play.twirl.api.Txt
import _root_.play.twirl.api.Xml
import models._
import controllers._
import play.api.i18n._
import views.html._
import play.api.templates.PlayMagic._
import java.lang._
import java.util._
import play.core.j.PlayMagicForJava._
import play.mvc._
import play.api.data.Field
import play.data._
import play.core.j.PlayFormsMagicForJava._
import scala.jdk.CollectionConverters._
/*1.2*/import play.mvc.Http.Request

object index extends _root_.play.twirl.api.BaseScalaTemplate[play.twirl.api.HtmlFormat.Appendable,_root_.play.twirl.api.Format[play.twirl.api.HtmlFormat.Appendable]](play.twirl.api.HtmlFormat) with _root_.play.twirl.api.Template3[Form[forms.LoginForm],Request,play.i18n.Messages,play.twirl.api.HtmlFormat.Appendable] {

  /**/
  def apply/*2.2*/(loginForm: Form[forms.LoginForm])(implicit request: Request, messages: play.i18n.Messages):play.twirl.api.HtmlFormat.Appendable = {
    _display_ {
      {


Seq[Any](format.raw/*3.1*/("""
"""),_display_(/*4.2*/main("Turboflix! - Access your account")/*4.42*/ {_display_(Seq[Any](format.raw/*4.44*/("""
    """),format.raw/*5.5*/("""<div class="login">
        <h1>Turboflix! Knowledge Now</h1>
        <div class="alert alert-success mt-5" role="alert">
            Please enter your credentials
        </div>
        """),format.raw/*10.44*/("""
        """),_display_(/*11.10*/request/*11.17*/.flash.asScala().data.map/*11.42*/ { case (name, value) =>_display_(Seq[Any](format.raw/*11.66*/("""
            """),format.raw/*12.13*/("""<div class=""""),_display_(/*12.26*/name),format.raw/*12.30*/("""">"""),_display_(/*12.33*/value),format.raw/*12.38*/("""</div>
        """)))}),format.raw/*13.10*/("""
        """),format.raw/*14.9*/("""<div class="d-flex p-2 bd-highlight mt-5 form">
        """),_display_(/*15.10*/b4/*15.12*/.vertical.formCSRF(routes.LoginController.submit() )/*15.64*/ { implicit vfc =>_display_(Seq[Any](format.raw/*15.82*/("""
            """),_display_(/*16.14*/b4/*16.16*/.email( loginForm("username"), Symbol("_label") -> "Username", Symbol("placeholder") -> "example@mail.com" )),format.raw/*16.124*/("""
            """),_display_(/*17.14*/b4/*17.16*/.password( loginForm("password"), Symbol("_label") -> "Password", Symbol("placeholder") -> "Password" )),format.raw/*17.119*/("""
            """),_display_(/*18.14*/b4/*18.16*/.submit(Symbol("class") -> "btn btn-default w-100 text-white bg-primary")/*18.89*/{_display_(Seq[Any](format.raw/*18.90*/(""" """),format.raw/*18.91*/("""Login """)))}),format.raw/*18.98*/("""
        """)))}),format.raw/*19.10*/("""
        """),format.raw/*20.9*/("""</div>
    </div>
""")))}))
      }
    }
  }

  def render(loginForm:Form[forms.LoginForm],request:Request,messages:play.i18n.Messages): play.twirl.api.HtmlFormat.Appendable = apply(loginForm)(request,messages)

  def f:((Form[forms.LoginForm]) => (Request,play.i18n.Messages) => play.twirl.api.HtmlFormat.Appendable) = (loginForm) => (request,messages) => apply(loginForm)(request,messages)

  def ref: this.type = this

}


              /*
                  -- GENERATED --
                  SOURCE: app/views/login/index.scala.html
                  HASH: d2e5eb150bf47e636f4691bf53d199617b97046e
                  MATRIX: 616->1|991->31|1176->123|1203->125|1251->165|1290->167|1321->172|1536->394|1573->404|1589->411|1623->436|1685->460|1726->473|1766->486|1791->490|1821->493|1847->498|1894->514|1930->523|2014->580|2025->582|2086->634|2142->652|2183->666|2194->668|2324->776|2365->790|2376->792|2501->895|2542->909|2553->911|2635->984|2674->985|2703->986|2741->993|2782->1003|2818->1012
                  LINES: 23->1|28->2|33->3|34->4|34->4|34->4|35->5|40->10|41->11|41->11|41->11|41->11|42->12|42->12|42->12|42->12|42->12|43->13|44->14|45->15|45->15|45->15|45->15|46->16|46->16|46->16|47->17|47->17|47->17|48->18|48->18|48->18|48->18|48->18|48->18|49->19|50->20
                  -- GENERATED --
              */
          