/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gtango

import spock.lang.*

class TangoUnitBuilderSpock extends Specification {
  
  
  def "A new DSL for TangoUnit ?"() {
    setup:
    
    def tangounit = TangoUnitBuilder.newInstance()
    
    where:
    // Prepare Test device
    tangounit."EnergyScan"{
      starters{
        "tango/admin/hardion-VirtualBox" "linux"
      }
      programs{
        "ds_TangoTest" "TangoTest"
      }
      // Special balise for TangoUnit --> open a test session
      // testServer: the class to test
      test(Transformer){
        optimusPrime{         // variable to keep reference of the device  
          // Define Sub proxies
          pub1("Publisher"){  // pub1 a variable for the sub device :Server and class
              properties{   // setting properties
                attributeList """motor1position;DEVDOUBLE;SPECTRUM
                                   sensor1value;DEVDOUBLE;SPECTRUM"""
              }
            }
          [0..3].each{
            publisher"$it"("Publisher")
          }
          
          
          // Define the properties
          properties{         // properties of the test device
            dynamicAttributes """proxy@$pub1/sensor1value
                                 groovyProxy@$pub1/motor1position"""
            motor1position """value.collect(){it*10}.toArray() as double[]"""
          }
        }
      }
      // Also you can use independant device
      // other nodes than test are device
      // server: server of the subdevice (optional if equals to clazz)
      // clazz: class of the subdevice (optional if equals to Server)
      // instance
      other(device:"test/another/device", server:TangoTest, instance:test, clazz:TangoTest, os:linux){ 
        properties{
          nothing "NO thing"
          another "thing"
        }
        attributes{
          DevString{
            // Here the only node are properties
            display "DeV StrinG"
          }
        }
      }
      // same server than other
      sameServer(server:TangoTest, instance:test, clazz:TangoTest, os:linux)
      // Same server than other (same condition)
      sameOtherServer(TangoTest)
      // Error same serverand instance but different os
      error(server:TangoTest, instance:test, os:windows)
      
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
  
    println tangounit.session.deviceConfiguration
    // Prepare Test device
   /* tangounit.setTestServer("Transformer");
    TRANSFORMER = tangounit.addTestDevice("Publisher").getName();

    // Prepare Proxy
		PUBLISHER = tangounit.addDevice("Publisher").getName();
		tangounit.addDeviceProperties(PUBLISHER, "AttributesList",
      new String[] { "motor1position;DEVDOUBLE;SPECTRUM",
						"sensor1value;DEVDOUBLE;SPECTRUM" });

    // Configure my Transformer
    tangounit.addDeviceProperties(TRANSFORMER, "dynamicAttributes", new String[]{
            "proxy@"+PUBLISHER+"/sensor1value",
            "groovyProxy@"+PUBLISHER+"/motor1position"
      });
    tangounit.addDeviceProperties(TRANSFORMER, "motor1position", new String[]{
            "value.collect(){it*10}.toArray() as double[]"
      });

    // Have to
		tangounit.create().start();
		tangounit.executeTestDevice();
    */

    expect:
    tangounit.session.deviceConfiguration == ["TEST"]
  
  }
  
/*  def "or this DSL ?"() {
    setup:
    
    def group = DeviceGroupBuilder.newInstance()
    
    where:
    
    group."EnergyScan"{
      NI6602(name:"scan/energy/cpt"){
        properties{
          counter1 "motorA"
          counter2 """motorB
                   type=1"""
          counter3 """event"""
        }
        attributes{
          counter1{
            name "energy"
          }
        }        
      }
    }
    
    

    expect:

    
  }
  */
}

