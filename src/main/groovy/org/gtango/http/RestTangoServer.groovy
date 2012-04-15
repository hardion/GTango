#!/usr/bin/env groovy -classpath org.restlet.jar:com.noelios.restlet.jar
package org.gtango.http

import org.lpny.groovyrestlet.builder.RestletBuilder;
import org.restlet.*  
import org.restlet.data.* 

import fr.esrf.TangoApi.DeviceAttribute

import org.gtango.client.DeviceServerProxy

/**
 * Export Tango Devices and directory as HTTP url
 * 
 * Based on REST, only few requests are necessary
 * - read attribute = (GET)
 * - write attribute = (PUT)
 * - add properties (POST)
 * - delete properties (DELETE)
 * - execute commands (GET with parameters)
 *
 * 
 * Given REST and other sweet libraries
 * - GroovyRestlet
 * - http://json-lib.sourceforge.net/groovy.html
 * 
 * A richer solution could be Grails ...
 * 
 * @author hardion
 */
//System.setProperty("TANGO_HOST","192.168.56.2:20001")

// CLI Management
cli = new CliBuilder(usage:'RestTangoServer [options]')
cli.h(longOpt:'help', 'print this message')
cli._(longOpt:'simulate', 'Use Simulated devices')
cli.th(longOpt:'tango_host', 'Set the TANGO_HOST (by default try with Environnement variable) ')
cli.p(longOpt:'httpPort', args:1, argName:'value', 'HTTP PORT')
cli.c(longOpt:'context', args:1, argName:'value', 'Context of the server ; useful for reverse proxy (ie: http://myserver:port/context/)')
cli.q('If used as the first parameter disables .curlrc')
cli._(longOpt:'url', args:1, argName:'URL', 'Set URL to work with')
options = cli.parse(args)
if (options.h) {
  cli.usage();
  return
}
// _CLI

builder = new RestletBuilder()

builder.setVariable("springContext" , null)
builder.init()

simulated = options.simulate
if(!simulated){
  def th  = options.th ?: System.env["TANGO_HOST"]
  
  if(th){
    System.setProperty("TANGO_HOST", th)
     db = new fr.esrf.TangoApi.Database()
  }
}

httpPort = (int)(options.p ?: 8182)
context = options.c ?: ""

builder.component{
  current.servers.add(Protocol.HTTP, httpPort)
  application(uri:context){
    router{
      restlet(uri:"/devices", handle:{req,resp ->
          def domains = db.get_device_domain("*")
          def value = domains.inject(""){str, item -> str+"\"$item\","}
          def result = """{ "domains":[${value[0..-2]}] }"""
          resp.setEntity(result,
            MediaType.APPLICATION_JSON)
        })
      restlet(uri:"/devices/{domain}", handle:{req,resp->
          def domain = req.attributes.get('domain')

          def domains = db.get_device_domain("*")
          def result = """{ "domains":"${value}" }"""
          resp.setEntity(result,
            MediaType.APPLICATION_JSON)
        })
      restlet(uri:"/devices/{domain}/{family}/{device}", handle:{req,resp->
          def domain = req.attributes.get('domain')
          def family = req.attributes.get('family')
          def device = req.attributes.get('device')
          def proxyname = "$domain/$family/$device"
          def d = new DeviceServerProxy(proxyname)
          def status = d.status()
          resp.setEntity("Status of Domain/Family/Device \"${proxyname} = ${status}\"",
            MediaType.TEXT_PLAIN)
        })
      restlet(uri:"/devices/{domain}/{family}/{device}/{element}", handle:{req,resp->
                    
          def domain = req.attributes.get('domain')
          def family = req.attributes.get('family')
          def device = req.attributes.get('device')
          def element = req.attributes.get('element')

          def proxyname = "$domain/$family/$device"
          

          // Check if command GET + PARAMETERS
          def form = req.resourceRef.queryAsForm;
          def result = ""
          def params = [:]
          for (Parameter parameter : form) {
            params."${parameter.name}" = parameter.value
            result += "parameter " + parameter.name;
            result += "/" + parameter.value;
            println "PARAMS= $result"
          }
          

          //TANGO PART
          def gtlet = simulated?new SimulatedRestlet(proxyname):new TangoRestlet(proxyname)
          def value = null
          if(params.size() != 0){
            value = gtlet.execute()
          }else{
            value = gtlet.read()
          }
          
          result = "{ \"value\":\"${value}\" }"

          resp.setEntity(result,
            MediaType.APPLICATION_JSON)
        })
    }
  }
}.start()


class SimulatedRestlet {
  
  int max=100

  SimulatedRestlet(def proxy){}
  
  def read(def attribute){
    Math.random()
  }
  
  def write(def attribute, def value){
    max = Integer.valueOf(value.toString())
  }
  
  def execute(def command, def parameters){
    Math.random()*100
  }
}

class TangoRestlet {
  DeviceServerProxy d

  TangoRestlet(def proxy){
    def d = new DeviceServerProxy(proxy)
  }

  def read(def proxy, def attribute){
    InsertExtractUtils.extractRead(d."$attribute")
  }
  
  def write(def proxy, def attribute, def value){
    d."$attribute" = value
  }
  
  def execute(def proxy, def command, def parameters){
    if(parameters."void" != null){
      d."$element"()
    }else{
      d."$element"(parameters["param1"])
    }
  }
  
}