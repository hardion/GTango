/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gtango.test
    
import fr.esrf.Tango.DevError;
import fr.esrf.Tango.DevFailed;
import fr.esrf.TangoApi.ApiUtil;
import fr.soleil.tangounit.device.Factories
import fr.soleil.tangounit.device.Starter

import org.gtango.test.TangoUnitBuilder

System.setProperty("TANGO_HOST","hardion-VirtualBox.local:20001")
//System.setProperty("TANGO_HOST","192.168.56.2:20001")

//try {
//			ApiUtil.get_db_obj().setAccessControl(TangoConst.ACCESS_WRITE);
//		} catch (DevFailed e) {
//      e.errors.each(){ DevError err ->
//        println err.severity
//        println err.desc
//        println err.origin
//        println err.reason
//      }
//			e.printStackTrace();
//		}
//Factories.addStarter("tango/admin/hardion-VirtualBox",Starter.Platform.linux)

def tangounit = TangoUnitBuilder.newInstance()
    
    // Prepare Test device
    tangounit.session{
      starters{
        "tango/admin/hardion-VirtualBox" "linux"
      }
      programs{
        "ds_TangoTest" "TangoTest"
      }

      // Special balise for TangoUnit --> open a test session
      // testServer: the class to test
      test("Transformer"){
        optimusPrime(){         // variable to keep reference of the device  
          // Define Sub proxies
          pub1("Publisher"){  // pub1 a variable for the sub device :Server and class
              properties{   // setting properties
                "AttributeList" """motor1position;DEVDOUBLE;SPECTRUM
                                 sensor1value;DEVDOUBLE;SPECTRUM"""
              }
          }
//          Doesn't work
//          [0..3].each{
//            publisher"$it"("Publisher")
//          }
          
          
          // Define the properties
          properties{         // properties of the test device
            "DynamicAttributes" """proxy@${pub1.name}/sensor1value
                                 groovyProxy@${pub1.name}/motor1position"""
            "Motor1position" """value.collect(){it*10}.toArray() as double[]"""
          }
        }
      }
      // Also you can use independant device
      // other nodes than test are device
      // server: server of the subdevice (optional if equals to clazz)
      // clazz: class of the subdevice (optional if equals to Server)
      // instance
      other(device:"test/another/device", clazz:"TangoTest", instance:"test", label:"linux"){ 
        properties{
          nothing "N thing"
          another "An other thing"
        }
//          TODO
//          attributes{
//          DevString{
//            // Here the only node are properties
//            display "DeV StrinG"
//          }
//        }
      }
      // same server than other
      //TODO doesn't work device=null
      sameServer(clazz:"TangoTest", instance:"test",  label:"linux")
      
      // Same server than other (same condition)
      sameOtherServer("TangoTest")
      
      // Error same serverand instance but different os
      //TODO parameters doesn't support ?
      /*
      error(clazz:"TangoTest", instance:"test", label:"win32")
      */
     
      // Another way
      /*Server("TangoTest"){ 
        device1
        device2{
          properties{
            stuff "biodegradable"
          }
        }
        device3("test/another/way")
        
      }
      // Third way
      Clazz("TangoParser"){ 
        device4{
          proxies{
            Server("TangoTest")
          }
        }
      }
      */
    }
  
 tangounit.session.devicesConfiguration.each { it.each{ d -> println d;println "-------" }}
 
println tangounit.optimusPrime.name
println tangounit.pub1.name
println tangounit.other.name
println tangounit.sameServer.name
println tangounit.sameOtherServer.name

tangounit.session.create().start();
tangounit.session.executeTestDevice();