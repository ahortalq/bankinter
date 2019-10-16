// Exported from:        http://kubuntu:5516/#/templates/Folder69f85172321f46a89580c80c09103603-Folder0c3ec44ba3194f6d932dc97005e007c5-Release738db3648318407b846722fe2f21f7d5/releasefile
// XL Release version:   9.0.6
// Date created:         Wed Oct 16 19:56:37 CEST 2019

xlr {
  template('4.2 Permitir establecer dependencias de despliegue entre los paquetes o componentes unitarios. (2)') {
    folder('Workshop/Bankinter')
    variables {
      mapVariable('dependencies') {
        label 'Dependencias de microservicios'
        description 'Establezca las dependencias de este microservicio con terceros'
        value 'microservicio2':'2.0','microservicio3':'3.0','microservicio1':'1.0'
      }
    }
    scheduledStartDate Date.parse("yyyy-MM-dd'T'HH:mm:ssZ", '2019-10-16T09:00:00+0200')
    phases {
      phase('DESARROLLO') {
        color '#0099CC'
        tasks {
          script('Obtener dependencias') {
            script (['''\
import urllib2
import json
import base64
import time

for key, val in ${dependencies}.items():
    print("key = {0}".format(key))
    print("val = {0}".format(val))
    url = "http://localhost:5516/api/v1/releases/byTitle?releaseTitle={0}%20{1}".format(key, val)
    req = urllib2.Request(url)
    base64string = base64.b64encode("{0}:{1}".format('admin', '2001jcla'))
    req.add_header("Authorization", "Basic %s" % base64string)
    req.add_header('Content-Type','application/json')
    microserviceFound = False
    while not microserviceFound:
        response = urllib2.urlopen(req)
        data = json.load(response)
        print("Total de registros para microservicio {0} en version {1} = {2}".format(key, val, len(data)))
        if len(data) > 0:
            microserviceFound = True
        else:
            print("Microservice no encontrado, esperamos 5 segundos")
            time.sleep(5)
'''])
          }
          manual('Compilación') {
            
          }
          manual('Ejecución test unitarios') {
            
          }
          manual('Aprobación') {
            
          }
        }
      }
      phase('PRUEBAS') {
        color '#08B153'
        tasks {
          manual('Pruebas cheking qa') {
            
          }
          manual('Pruebas de performance') {
            
          }
          manual('Aprobación') {
            
          }
        }
      }
      phase('PREPRODUCCIÓN') {
        color '#FD8D10'
        tasks {
          manual('Revisión versión liberada') {
            
          }
          gate('Espera por dependencias') {
            
          }
          manual('Despliegue a PRE') {
            
          }
          manual('Ejecución pruebas de aceptación') {
            
          }
          manual('Aprobación pase a PRO') {
            
          }
        }
      }
      phase('PRODUCCIÓN') {
        color '#D94C3D'
        tasks {
          manual('Reunión de lanzamiento') {
            
          }
          gate('Espera por dependencias') {
            
          }
          manual('Despliegue a PRO') {
            
          }
          manual('Notificación') {
            
          }
        }
      }
    }
    
  }
}