package chrome


import OptionPickler.{macroRW, readwriter, writeJs, ReadWriter => RW}

case class Udp(bind: Set[String] = Set(),
               send: Set[String] = Set(),
               multicastMembership: Set[String] = Set())
object Udp{
  implicit val rw: RW[Udp] = macroRW
}
case class Tcp(connect: Set[String] = Set(), listen: Set[String] = Set())
object Tcp{
  implicit val rw: RW[Tcp] = macroRW
}

case class Sockets(udp: Option[Udp] = None, tcp: Option[Tcp] = None)

object Sockets{
  implicit val rw: RW[Sockets] = {
    readwriter[ujson.Value].bimap[Sockets](sockets => {
      ujson.Obj(
        "udp" ->  writeJs(sockets.udp),
        "tcp" ->  writeJs(sockets.tcp.map(_.connect)),
        "tcpServer" ->  writeJs(sockets.tcp.map(_.listen))
      )
    },json => ???)
  }
}
