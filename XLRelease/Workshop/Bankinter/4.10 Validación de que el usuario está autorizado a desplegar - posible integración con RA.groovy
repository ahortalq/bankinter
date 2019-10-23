// Exported from:        http://kubuntu:5516/#/templates/Folder69f85172321f46a89580c80c09103603-Folder0c3ec44ba3194f6d932dc97005e007c5-Releaseb01136c233b3452e84a12dc5940bb545/releasefile
// XL Release version:   9.0.6
// Date created:         Wed Oct 23 08:01:19 CEST 2019

xlr {
  template('4.10 Validación de que el usuario está autorizado a desplegar - posible integración con RA') {
    folder('Workshop/Bankinter')
    variables {
      stringVariable('user') {
        required false
        showOnReleaseStart false
        label 'Usuario'
        description 'Usuario para CA Release Automation'
      }
      passwordVariable('password') {
        required false
        showOnReleaseStart false
        label 'Password'
        description 'Password para CA Release Automation'
      }
      stringVariable('user_pro') {
        required false
        showOnReleaseStart false
        label 'Usuario'
        description 'Usuario CA Release Automation'
      }
      passwordVariable('password_pro') {
        required false
        showOnReleaseStart false
        label 'Password'
        description 'Password CA Release Automation'
      }
    }
    scheduledStartDate Date.parse("yyyy-MM-dd'T'HH:mm:ssZ", '2019-10-17T09:00:00+0200')
    phases {
      phase('BUILD') {
        color '#0099CC'
      }
      phase('INTEGRACIÓN') {
        color '#999999'
      }
      phase('PREPRODUCCIÓN') {
        color '#08B153'
        tasks {
          userInput('Credenciales para el despliegue en PRE') {
            description 'Please enter the required information below.'
            team 'Desarrollo'
            variables {
              variable 'user'
              variable 'password'
            }
          }
          custom('Despliegue en PRE') {
            script {
              type 'nolio.runDeployment'
              nolioServer 'CA Release Automation Server'
              username '${user}'
              password variable('password')
              deploymentResult false
            }
          }
        }
      }
      phase('PRODUCCIÓN') {
        color '#FD8D10'
        tasks {
          userInput('Credenciales para el despliegue en PRO') {
            description 'Please enter the required information below.'
            team 'Operaciones'
            variables {
              variable 'user_pro'
              variable 'password_pro'
            }
          }
          custom('Despliegue en PRO') {
            script {
              type 'nolio.runDeployment'
              nolioServer 'CA Release Automation Server'
              username '${user_pro}'
              password variable('password_pro')
              deploymentResult false
            }
          }
        }
      }
    }
    
  }
}