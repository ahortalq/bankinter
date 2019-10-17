// Exported from:        http://kubuntu:5516/#/templates/Folder69f85172321f46a89580c80c09103603-Folder0c3ec44ba3194f6d932dc97005e007c5-Releasee679c4da44504ee585a39f4d91ac73ab/releasefile
// XL Release version:   9.0.6
// Date created:         Thu Oct 17 11:26:45 CEST 2019

xlr {
  template('4.2 Permitir establecer dependencias de despliegue entre los paquetes o componentes unitarios. (3)') {
    folder('Workshop/Bankinter')
    variables {
      mapVariable('dependencies') {
        label 'Dependencias de microservicios'
        description 'Establezca las dependencias de este microservicio con terceros'
        value 'microservicio2':'2.0','microservicio3':'3.0','microservicio1':'1.0'
      }
      listVariable('id_phases') {
        required false
        showOnReleaseStart false
      }
    }
    scheduledStartDate Date.parse("yyyy-MM-dd'T'HH:mm:ssZ", '2019-10-16T09:00:00+0200')
    scriptUsername 'admin'
    scriptUserPassword '{aes:v0}DhvzVgm26Pl8DWRMiPFfi1CRPGoi7oa/nynEXeAoGrA='
    phases {
      phase('GET DEPENDENCIES') {
        color '#0099CC'
        tasks {
          custom('Obtener dependencias de los microservicios') {
            script {
              type 'dependencies.GetDependencies'
              password variable('folder.xlrAdminPassword')
              phaseName 'PRUEBAS'
              dependencies variable('dependencies')
              listIdsDependentPhases variable('id_phases')
            }
          }
          custom('Establecer dependencias') {
            script {
              type 'dependencies.SetDependencies'
              password variable('folder.xlrAdminPassword')
              phaseName 'WAIT FOR DEPENDENCIES'
              taskName 'Esperar cumplimiento de dependencias'
              listIdsDependentPhases variable('id_phases')
            }
          }
        }
      }
      phase('WAIT FOR DEPENDENCIES') {
        color '#D94C3D'
        tasks {
          gate('Esperar cumplimiento de dependencias') {
            
          }
        }
      }
    }
    
  }
}