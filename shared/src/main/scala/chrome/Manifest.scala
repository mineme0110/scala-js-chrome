package chrome

import chrome.permissions.Permission
import OptionPickler.{macroRW, ReadWriter => RW}
import OptionPickler._
import ujson.Obj
sealed trait Manifest {
  val name: String
  val version: String
  val manifestVersion: Int = 2
  val shortName: Option[String] = None
  val defaultLocale: Option[String] = None
  val description: Option[String] = None
  val offlineEnabled: Option[Boolean] = None
  val permissions: Set[Permission] = Set()
  val optionalPermissions: Set[Permission] = Set()
  val icons: Map[Int, String] = Map.empty
  val minimumChromeVersion: Option[String] = None
  val author: Option[String] = None
  val commands: Option[Commands] = None
  val key: Option[String] = None
  val storage: Option[Storage] = None
  val updateUrl: Option[String] = None
  val versionName: Option[String] = None
  val platforms: List[Platform] = Nil
  val externallyConnectable: Option[ExternallyConnectable] = None
  val oauth2: Option[Oauth2Settings] = None
  val webAccessibleResources: List[String] = Nil
  val contentSecurityPolicy: Option[String] = None
}


case class Background(scripts: List[String])
object Background{
  implicit val rw: RW[Background] = macroRW
}

case class App(background: Background)
object App{
  implicit val rw: RW[App] = macroRW
}

case class BrowserAction(
                          @upickle.implicits.key("default_icon") icon: Map[Int, String] = Map.empty,
                          @upickle.implicits.key("default_title") title: Option[String] = None,
                          @upickle.implicits.key("default_popup") popup: Option[String] = None
)

object BrowserAction{
  implicit val rw: RW[BrowserAction] = macroRW
}

case class ContentScript(
    matches: List[String],
    css: List[String],
    js: List[String])

object ContentScript{
  implicit val rw: RW[ContentScript] = macroRW
}

case class Bluetooth(
    uuids: List[String] = Nil,
    socket: Option[Boolean] = None,
    @upickle.implicits.key("low_energy") lowEnergy: Option[Boolean] = None,
    peripheral: Option[Boolean] = None
)
object Bluetooth{
  implicit val rw: RW[Bluetooth] = macroRW
}

case class Requirements(webgl: Option[Boolean] = None,
                        npapi: Option[Boolean] = None)

object Requirements{
  implicit val rw: RW[Requirements] = macroRW
}
case class Omnibox(keyword: String)
object Omnibox{
  implicit val rw: RW[Omnibox] = macroRW
}
case class Storage(@upickle.implicits.key("managed_schema") managedSchema: String)
object Storage{
  implicit val rw: RW[Storage] = macroRW
}

case class BookmarksUI(
        @upickle.implicits.key("remove_button") removeButton: Option[Boolean] = None,
        @upickle.implicits.key("remove_bookmark_shortcut") removeBookmarkShortcut: Option[Boolean] = None
                      )
object BookmarksUI{
  implicit val rw: RW[BookmarksUI] = macroRW
}

case class ChromeUIOverrides(newtab: String, @upickle.implicits.key("bookmarks_ui") bookmarksUI: BookmarksUI)

object ChromeUIOverrides{
  implicit val rw: RW[ChromeUIOverrides] = macroRW
}


case class Platform(
                     @upickle.implicits.key("nacl_arch") naclArch: String,
                     @upickle.implicits.key("sub_package_path") subPackagePath: String
)
object Platform{
  implicit val rw: RW[Platform] = macroRW
}
case class OptionsUI(
    page: String,
    @upickle.implicits.key("chrome_style") chromeStyle: Option[Boolean] = None
)
object OptionsUI{
  implicit val rw: RW[OptionsUI] = macroRW
}

case class ExternallyConnectable(
    matches: Set[String],
    ids: Set[String],
    @upickle.implicits.key("accepts_tls_channel_id") acceptsTlsChannelId: Option[Boolean]
)
object ExternallyConnectable{
  implicit val rw: RW[ExternallyConnectable] = macroRW
}


case class Commands(actions: Map[String, Commands.Action] = Map.empty)

object Commands {
  case class Action(
                     @upickle.implicits.key("suggested_key") suggestedKey: SuggestedKey,
      description: Option[String] = None,
      global: Option[Boolean] = None
  )

  case class SuggestedKey(
      default: Option[String] = None,
      linux: Option[String] = None,
      chromeos: Option[String] = None,
      mac: Option[String] = None,
      windows: Option[String] = None
  )
  val ExecuteBrowserAction = "_execute_browser_action"
  val ExecutePageAction = "_execute_page_action"

  implicit val actionRW: RW[Action] = macroRW
  implicit val suggestedKeyRW: RW[SuggestedKey] = macroRW
  implicit val rw: RW[Commands] = macroRW
}



case class Oauth2Settings(
                           @upickle.implicits.key("client_id") clientId: String,
                          scopes: List[String])
object Oauth2Settings{
  implicit val rw: RW[Oauth2Settings] = macroRW
}

object Manifest{
  def manifestToJson(manifest:Manifest): Obj = {
    ujson.Obj(
      "manifest_version" -> writeJs(manifest.manifestVersion),
      "name" -> writeJs(manifest.name),
      "version" -> writeJs(manifest.version),
      "default_locale" -> writeJs(manifest.defaultLocale),
      "description" -> writeJs(manifest.description),
      "icons" -> writeJs(manifest.icons),
      "author" -> writeJs(manifest.author),
      "commands" -> writeJs(manifest.commands),
      "key" -> writeJs(manifest.key),
      "offline_enabled" -> writeJs(manifest.offlineEnabled),
      "version_name" -> writeJs(manifest.versionName),
      "update_url" -> writeJs(manifest.updateUrl),
      "short_name" -> writeJs(manifest.shortName),
      "minimum_chrome_version" -> writeJs(manifest.minimumChromeVersion),
      "storage" -> writeJs(manifest.storage),
      "platforms" -> writeJs(manifest.platforms),
      "oauth2" -> writeJs(manifest.oauth2),
      "web_accessible_resources" -> writeJs(manifest.webAccessibleResources),
      "permissions" -> writeJs(manifest.permissions),
      "optional_permissions" -> writeJs(manifest.optionalPermissions),
      "content_security_policy" -> writeJs(manifest.contentSecurityPolicy)
    )
  }
}


trait AppManifest extends Manifest {
  val app: chrome.App
  val sockets: Option[Sockets] = None
  val bluetooth: Option[Bluetooth] = None
  val kioskEnabled: Option[Boolean] = None
  val kioskOnly: Option[Boolean] = None
}
object AppManifest{ //TODO
  implicit val rw: RW[AppManifest] = {
    readwriter[ujson.Value].bimap[AppManifest](am => {
     val appManifest = ujson.Obj(
        "app" -> writeJs(am.app),
        "bluetooth" -> writeJs(am.bluetooth),
        "kiosk_enabled" -> writeJs(am.kioskEnabled),
        "kiosk_only" -> writeJs(am.kioskOnly),
        "sockets" -> writeJs(am.sockets))
      val manifest = Manifest.manifestToJson(am)
      manifest.value ++ appManifest.value
    },_ => ???)
  }
}

 trait ExtensionManifest extends Manifest {
  val background: Background
  val browserAction: Option[BrowserAction] = None
  val omnibox: Option[Omnibox] = None
  val optionsUI: Option[OptionsUI] = None
  val chromeUIOverrides: Option[ChromeUIOverrides] = None
  val contentScripts: List[ContentScript] = List.empty
}
object ExtensionManifest{ //TODO
  implicit val rw: RW[ExtensionManifest] = {
    readwriter[ujson.Value].bimap[ExtensionManifest](em => {
     val extensionManifest =  ujson.Obj(
          "background" -> writeJs(em.background),
          "omnibox" -> writeJs(em.omnibox),
          "options_ui" -> writeJs(em.optionsUI),
          "browser_action" -> writeJs(em.browserAction),
          "chrome_ui_overrides" -> writeJs(em.chromeUIOverrides),
          "content_scripts" -> writeJs(em.contentScripts))
      val manifest = Manifest.manifestToJson(em)
      manifest.value ++ extensionManifest.value
    },json => ???)
  }
}
