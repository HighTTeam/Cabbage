package com.cabbage.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink


object AkkHttp extends App {


  override def main(args: Array[String]): Unit = {
    implicit val httpSys = ActorSystem("httpSys")
    implicit val httpMat = ActorMaterializer()
    implicit val httpEc = httpSys.dispatcher


    val (host,port) = ("localhost",8088)

    val serverSource = Http().bind(host, port)

    val bindingFuture = serverSource.to(Sink.foreach { connection =>
      println(s"accepted new connection from ${connection.remoteAddress}")
    }).run()

    println(s"Server running at $host $port. Press any key to exit ...")

    scala.io.StdIn.readLine()

    bindingFuture.flatMap(_.unbind())
      .onComplete(_ => httpSys.terminate())
  }
}
