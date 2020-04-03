import chrome.{App, AppManifest, Background, Bluetooth, BrowserAction, ExtensionManifest, Sockets, Tcp, Udp}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers._
import chrome.OptionPickler._
import chrome.permissions.Permission
import chrome.permissions.Permission.API
import chrome.permissions.Permission.API._
class ManifestJsonSpec extends AnyFlatSpec{
  val udp = Udp(send=Set("host-pattern1") , bind = Set("host-pattern2"))
  val tcp = Tcp(connect = Set("host-pattern1","host-pattern2"))
  val sockets = Sockets(udp=Some(udp))
  val background1 = Background(scripts = List("javascript"))

  it should "convert  Udp to  Manifest Json format" in {
    write(udp) mustBe """{"bind":["host-pattern2"],"send":["host-pattern1"]}"""
  }

  it should "convert  Tcp to Manifest Json format" in {
    write(tcp) mustBe """{"connect":["host-pattern1","host-pattern2"]}"""
  }

  it should "convert  Sockets to  Manifest Json format" in {
    write(sockets) mustBe """{"udp":{"bind":["host-pattern2"],"send":["host-pattern1"]},"tcp":null,"tcpServer":null}"""
  }

  it should "convert  Map to  Manifest Json format" in {
    val icons: Map[Int, String] = Map(128 -> "icon.png",129 -> "icon2.png")
    val icons1: Map[String, String] = Map("128" -> "icon.png","129" -> "icon2.png")
    write(icons) mustBe """{"128":"icon.png","129":"icon2.png"}"""
    write(icons1) mustBe """{"128":"icon.png","129":"icon2.png"}"""
  }

  it should "convert  ExtensionManifest to  ManifestJson format" in {
    val icons: Map[Int, String] = Map(128 -> "icon.png",129 -> "icon2.png")
    val browserAction1 = BrowserAction(icon = icons,title = Some("my-title"),popup = Some("popup"))
    val permissionSet: Set[Permission] = Set(API.ActiveTab,API.Bookmarks,API.DNS,
      WebNavigation,Wallpaper,System.Network,System.Display,
      Networking.Config,Downloads,Downloads.Shelf)
    val  ef = new ExtensionManifest {
      override val background: Background = background1
      override val browserAction: Option[BrowserAction] = Some(browserAction1)
      override val name: String = "chrome-app"
      override val version: String = "1.0.0"
      override val icons: Map[Int,String] =  Map(128 -> "icon.png",129 -> "icon2.png")
      override val manifestVersion: Int = 2
      override  val shortName: Option[String] = Some("short-name")
      override val defaultLocale: Option[String] = Some("en_US")
      override val description: Option[String] = Some("description")
      override val offlineEnabled: Option[Boolean] = Some(true)
      override val permissions: Set[Permission] = permissionSet
    }
    write(ef) mustBe """{"manifest_version":2,"name":"chrome-app","version":"1.0.0","default_locale":"en_US","description":"description","icons":{"128":"icon.png","129":"icon2.png"},"author":null,"commands":null,"key":null,"offline_enabled":true,"version_name":null,"update_url":null,"short_name":"short-name","minimum_chrome_version":null,"storage":null,"platforms":[],"oauth2":null,"web_accessible_resources":[],"permissions":["system.network","dns","downloads","system.display","activeTab","webNavigation","networking.config","wallpaper","downloads.shelf","bookmarks"],"optional_permissions":[],"content_security_policy":null,"background":{"scripts":["javascript"]},"omnibox":null,"options_ui":null,"browser_action":{"default_icon":{"128":"icon.png","129":"icon2.png"},"default_title":"my-title","default_popup":"popup"},"chrome_ui_overrides":null,"content_scripts":[]}"""
  }

  it should "convert  AppManifest to  ManifestJson format" in {
    val sockets1 = Sockets(udp=Some(udp),tcp=Some(tcp))
    val permissionSet: Set[Permission] = Set(API.ActiveTab,API.Bookmarks,API.DNS,
      WebNavigation,Wallpaper,System.Network,System.Display,
      Networking.Config,Downloads,Downloads.Shelf)
    val  am = new AppManifest {
      override val app: chrome.App = App(background1)
      override val name: String = "chrome-app"
      override val version: String = "1.0.0"
      override val icons: Map[Int,String] =  Map(128 -> "icon.png",129 -> "icon2.png")
      override val manifestVersion: Int = 2
      override  val shortName: Option[String] = Some("short-name-value")
      override val defaultLocale: Option[String] = Some("en_US")
      override val description: Option[String] = Some("description-value")
      override val offlineEnabled: Option[Boolean] = Some(true)
      override val sockets: Option[Sockets] = Some(sockets1)
      override val bluetooth: Option[Bluetooth] = None
      override val kioskEnabled: Option[Boolean] = None
      override val kioskOnly: Option[Boolean] = None
      override val permissions: Set[Permission] = permissionSet
    }
    write(am) mustBe
      """{"manifest_version":2,"name":"chrome-app","version":"1.0.0","default_locale":"en_US","description":"description-value","icons":{"128":"icon.png","129":"icon2.png"},"author":null,"commands":null,"key":null,"offline_enabled":true,"version_name":null,"update_url":null,"short_name":"short-name-value","minimum_chrome_version":null,"storage":null,"platforms":[],"oauth2":null,"web_accessible_resources":[],"permissions":["system.network","dns","downloads","system.display","activeTab","webNavigation","networking.config","wallpaper","downloads.shelf","bookmarks"],"optional_permissions":[],"content_security_policy":null,"app":{"background":{"scripts":["javascript"]}},"bluetooth":null,"kiosk_enabled":null,"kiosk_only":null,"sockets":{"udp":{"bind":["host-pattern2"],"send":["host-pattern1"]},"tcp":["host-pattern1","host-pattern2"],"tcpServer":[]}}""".stripMargin
  }
}
