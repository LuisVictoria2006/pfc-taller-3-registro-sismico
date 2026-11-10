package taller

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
@RunWith(classOf[JUnitRunner])
class MisPruebasTest extends AnyFunSuite {

  val s = new RegistroSismico()
  import s._

  val mio: List[Sismo] = List(
    Sismo(Fecha(2026, 1), "Cauca", 4.0, 10),
    Sismo(Fecha(2026, 1), "Narino", 5.5, 80),
    Sismo(Fecha(2026, 2), "Cauca", 6.2, 15),
    Sismo(Fecha(2026, 2), "Huila", 4.4, 60),
    Sismo(Fecha(2026, 3), "Narino", 3.5, 90),
    Sismo(Fecha(2026, 3), "Cauca", 5.5, 12)
  )

  // Punto 1: consultas con for

  test("fuertes: los sismos sobre 5.0 conservan orden y repetidos") {

    assert(fuertes(mio, 5.0) == List("Narino", "Cauca", "Cauca"))

  }

  test("fuertes: con umbral 6.2 no sale el de 6.2 exacto") {

    assert(fuertes(mio, 6.2) == List())

  }

  test("fuertes: un registro vacío no tiene sismos fuertes") {

    assert(fuertes(List(), 1.0) == List())

  }


  test("enRegion: las magnitudes del Cauca en orden") {

    assert(enRegion(mio, "Cauca") == List(4.0, 6.2, 5.5))

  }

  test("enRegion: una región con un solo sismo") {

    assert(enRegion(mio, "Huila") == List(4.4))

  }

  test("paresCercanos: con 0.5 se acercan Cauca con Narino y Cauca con Huila") {

    assert(paresCercanos(mio, 0.5) == List(("Cauca", "Narino"), ("Cauca", "Huila")))

  }

  test("paresCercanos: con 0.0 solo cuentan las magnitudes idénticas") {

    assert(paresCercanos(mio, 0.0) == List(("Cauca", "Narino")))

  }

  test("paresCercanos: con 1.0 salen las tres parejas, cada una una vez") {

    assert(paresCercanos(mio, 1.0) == List(("Cauca", "Narino"), ("Cauca", "Huila"), ("Narino", "Huila")))

  }

  test("paresCercanos: con una sola región no hay parejas") {

    val unaRegion = List(
      Sismo(Fecha(2026, 1), "Cauca", 4.0, 10),
      Sismo(Fecha(2026, 2), "Cauca", 4.1, 10)
    )
    assert(paresCercanos(unaRegion, 1.0) == List())

  }

  // Punto 2: mapas

  test("porRegion: los sismos de Narino conservan su orden") {

    assert(porRegion(mio)("Narino").map(x => x.magnitud) == List(5.5, 3.5))

  }

  test("porRegion: las llaves son las tres regiones del registro") {

    assert(porRegion(mio).keySet == Set("Cauca", "Narino", "Huila"))

  }

  test("porRegion: una región que no está no aparece") {

    assert(!porRegion(mio).contains("Atlantico"))

  }

  test("conteoPorRegion: cuenta los sismos de cada región") {

    assert(conteoPorRegion(mio) == Map("Cauca" -> 3, "Narino" -> 2, "Huila" -> 1))

  }

  test("conteoPorRegion: los conteos suman el tamaño del registro") {

    assert(conteoPorRegion(mio).values.sum == mio.length)

  }

  test("magnitudMaxima: la mayor de cada región") {

    assert(magnitudMaxima(mio) == Map("Cauca" -> 6.2, "Narino" -> 5.5, "Huila" -> 4.4))

  }

  test("magnitudMaxima: un registro vacío da un mapa vacío") {

    assert(magnitudMaxima(List()) == Map())

  }

  test("conteoPorRegion: no depende del orden del registro") {

    assert(conteoPorRegion(mio.reverse) == conteoPorRegion(mio))

  }

  test("magnitudMaxima: no depende del orden del registro") {

    assert(magnitudMaxima(mio.reverse) == magnitudMaxima(mio))

  }

  // Punto 3: consultas seguras

  test("conteoSeguro: una región desconocida responde cero") {

    assert(conteoSeguro(mio)("Atlantico") == 0)


  }

  test("conteoSeguro: una región conocida responde su conteo") {

    assert(conteoSeguro(mio)("Cauca") == 3)

  }

  test("conteoSeguro: con el registro vacío todo responde cero") {

    assert(conteoSeguro(List())("Cauca") == 0)

  }

  test("maximaDe: una región con registros da Some") {

    assert(maximaDe(mio, "Narino") == Some(5.5))

  }

  test("maximaDe: una región sin registros da None") {

    assert(maximaDe(mio, "Atlantico") == None)

  }

  test("regionMasActiva: gana la que tiene más sismos") {

    assert(regionMasActiva(mio) == Some("Cauca"))

  }

  test("regionMasActiva: en empate gana la primera en orden alfabético") {

    val empate = List(
      Sismo(Fecha(2026, 1), "Boyaca", 4.0, 10),
      Sismo(Fecha(2026, 1), "Antioquia", 4.0, 10)
    )
    assert(regionMasActiva(empate) == Some("Antioquia"))

  }

  test("regionMasActiva: con una sola región gana esa") {

    assert(regionMasActiva(List(Sismo(Fecha(2026, 4), "Huila", 3.0, 5))) == Some("Huila"))

  }

  // Punto 4: réplicas perezosas

  test("replicas: empieza en 10.0 y cada una es el 80 % de la anterior") {

    assert(replicas(10.0).take(4).toList == List(10.0, 8.0, 6.4, 5.12))

  }

  test("replicas: la primera es la magnitud dada") {

    assert(replicas(7.5).head == 7.5)

  }

  test("replicas: se pueden pedir mil elementos de una secuencia infinita") {

    assert(replicas(5.0).take(1000).length == 1000)

  }

  test("replicas: ninguna réplica es mayor que la anterior") {

    val l = replicas(5.0).take(10).toList
    assert(l.zip(l.tail).forall(par => par._1 >= par._2))

  }

  test("replicasSensibles: con mínimo 5.0 desde 10.0") {

    assert(replicasSensibles(10.0, 5.0) == List(10.0, 8.0, 6.4, 5.12))

  }

  test("replicasSensibles: el mínimo es estricto") {

    assert(replicasSensibles(4.0, 4.0) == List())

  }

  test("replicasSensibles: con mínimo cero termina solo y todas son positivas") {

    val l = replicasSensibles(3.0, 0.0)
    assert(l.nonEmpty && l.forall(x => x > 0.0))

  }

  test("replicasSensibles: si la primera no supera el mínimo no hay ninguna") {

    assert(replicasSensibles(1.0, 2.0) == List())

  }

  // Punto 5: informe

  test("informe: resumen de cada región con mínimo 4.0") {

    assert(informe(mio, 4.0) == Map(
      "Cauca" -> Resumen(3, 6.2, 2),
      "Narino" -> Resumen(2, 5.5, 2),
      "Huila" -> Resumen(1, 4.4, 1)
    ))

  }

  test("informe: con un mínimo muy alto no se esperan réplicas") {

    assert(informe(mio, 7.0) == Map(
      "Cauca" -> Resumen(3, 6.2, 0),
      "Narino" -> Resumen(2, 5.5, 0),
      "Huila" -> Resumen(1, 4.4, 0)
    ))

  }

  test("informe: un registro con una sola región") {

    val uno = List(Sismo(Fecha(2026, 5), "Cesar", 5.0, 20))
    assert(informe(uno, 3.0) == Map("Cesar" -> Resumen(1, 5.0, 3)))

  }

  test("informe: tiene las mismas regiones que conteoPorRegion") {

    assert(informe(mio, 4.0).keySet == conteoPorRegion(mio).keySet)

  }

  test("informe: coincide con conteoSeguro y maximaDe") {

    val resumen = informe(mio, 4.0)("Cauca")
    assert(resumen.sismos == conteoSeguro(mio)("Cauca"))
    assert(maximaDe(mio, "Cauca") == Some(resumen.maxima))

  }

  test("informe: las réplicas son las de replicasSensibles sobre la máxima") {

    assert(informe(mio, 4.0)("Narino").replicas == replicasSensibles(5.5, 4.0).length)

  }

  test("informe: no depende del orden del registro") {

    assert(informe(mio.reverse, 4.0) == informe(mio, 4.0))
    
  }
}