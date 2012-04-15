package org.gtango.client

import fr.esrf.TangoApi.DeviceProxy
import fr.esrf.TangoApi.DeviceAttribute
import fr.esrf.TangoApi.DeviceData
import fr.esrf.Tango.DevFailed
import fr.esrf.TangoApi.CommunicationFailed
import fr.esrf.TangoDs.TangoConst
import fr.esrf.Tango.DevState
//@Grab(group='fr.esrf.tango', module='TangORB', version='7.3.1')


/**
 * A Tango Device Proxy
 * 
 * You can :
 * - read and write attribute by calling directly the attribute as a property
 * - execute command like you call a method
 * 
 * @vhardion
 */
class DeviceServerProxy{

    DeviceProxy proxy
    String address
    
    def printError={ it ->
        println it.desc
        println it.origin
        println it.reason
        println it.severity
    }

    DeviceServerProxy(String address){
        try{
            proxy = new DeviceProxy(address)
            
        }catch(DevFailed e){
            e.errors.each(printError)
        }
        this.address  = address
    }

    def invokeMethod(String name, args){
        try{
            DeviceData data = new DeviceData();
            if(args){
                data.insert(args[0])
                data=proxy.command_inout(name, data)
                
            }else{
                data=proxy.command_inout(name)
            }

            deviceDataConverter.get(data.getType())(data)
            
        }catch(DevFailed e){
            e.errors.each(printError)
        }
    }

    def getProperty(String name) {
            try{
                DeviceAttribute attr = proxy.read_attribute(name)                
            }catch(CommunicationFailed e){
                e.errors.each(printError)
            }
    }

    void setProperty(String name, value) {
      if(!value instanceof DeviceAttribute){
        value = new DeviceAttribute(value)
      }
        proxy.write_attribute(name, value)
    }
 
}