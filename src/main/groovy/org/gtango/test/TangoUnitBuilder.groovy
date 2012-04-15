/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gtango.test

import fr.soleil.tangounit.Session
import fr.soleil.tangounit.device.Server
import fr.soleil.tangounit.device.Device
import fr.soleil.tangounit.device.Starter
import fr.soleil.tangounit.device.Starter.Platform
import fr.soleil.tangounit.device.Property
import fr.soleil.tangounit.device.Factories
import fr.esrf.Tango.DevError;
import fr.esrf.Tango.DevFailed;

/**
 *
 * @author hardion
 */
class TangoUnitBuilder extends FactoryBuilderSupport {
  
  Factory defaultt = new TangoDeviceFactory()
  
  static TangoUnitBuilder newInstance(){
    return new TangoUnitBuilder();
  }
  
  protected TangoUnitBuilder(){
    super(false)
    registerAll()
  }
  
  public void registerAll() {
    registerFactory("session", new TangoUnitSessionFactory());
    registerFactory("test", new TangoUnitTestFactory());
    registerFactory("starters", new TangoUnitStarterFactory());
    registerFactory("programs", new TangoUnitProgramFactory());
    registerFactory("properties", new TangoPropertiesFactory());
    
    
  }
    
  protected Factory resolveFactory(Object name, Map attributes, Object value) {
    // let custom factories be resolved first
    Factory factory = super.resolveFactory(name, attributes, value);
    if (!factory /*&& name ==~ /[\\/\w\d]*/) {
      // Default
      factory = defaultt
    }
    println "Resolve factory $name <=> $factory"

    return factory;

  }

}

class TangoUnitSessionFactory extends AbstractFactory{
    
  //Responsible for creating the object that responds to the node 'name' and its called during builder.createNode
  Object newInstance( FactoryBuilderSupport builder, Object name, Object value, Map attributes ) throws InstantiationException, IllegalAccessException{
    Session result = new Session()
    builder.setVariable("session", result)
    println "Session created"
    return result
  }


  //Lets the builder know if the node allows for further nodes to be nested on the current node.
  boolean isLeaf(){
    return false;   
  }
}
class TangoUnitTestFactory extends AbstractFactory{
    
  //Responsible for creating the object that responds to the node 'name' and its called during builder.createNode
  Object newInstance( FactoryBuilderSupport builder, Object name, Object value, Map attributes ) throws InstantiationException, IllegalAccessException{
    Session test = (Session) builder.getVariable("session");
    Server result = test.setTestServer(value);
    println "**** Test Factory $result.name"
    return result;
  }


  //Lets the builder know if the node allows for further nodes to be nested on the current node.
  boolean isLeaf(){
    return false;   
  }
}

class TangoDeviceFactory extends AbstractFactory{
    
  //Responsible for creating the object that responds to the node 'name' and its called during builder.createNode
  Object newInstance( FactoryBuilderSupport builder, Object name, Object value, Map attributes ) throws InstantiationException, IllegalAccessException{
    Session tu = (Session) builder.getVariable("session");
    Device result = null
        println "----- TangiUnitDevice --> Parent Factory =${builder.parentFactory}"

    if(builder.parentFactory instanceof TangoUnitTestFactory){
      //tu.getTestServer() doestn't exist so we need 1 parameter to define the class
      String clazz = value ?: attributes["clazz"]?: tu.testServer.program

      result = tu.addTestDevice(clazz);

    }else{
      
      def clazz = value ?: attributes["clazz"]
      def device = attributes["device"]
      def host = attributes["host"]?:attributes["label"]
      def instance = attributes["instance"]
      def platform = attributes["label"]?:attributes["host"]
      println "----- TangiUnitDevice --> clazz=$clazz device=$device host=$host instance=$instance platform=$platform"

      if(clazz && platform && !instance && !device){
      println "        result = tu.addDevice(clazz, Starter.Platform.valueOf(platform))"
        result = tu.addDevice(clazz, Starter.Platform.valueOf(platform))
        
      }else if(clazz && host && instance && device){
      println "        result = tu.addDevice(clazz, host, instance, device)"
        result =  tu.addDevice(clazz, host, instance, device)

      }else if(clazz && host && instance){
      println "        result = tu.addDevice(clazz, host, instance)"
        result =  tu.addDevice(clazz, host, instance)

      }else if(clazz && host){
      println "        result = tu.addDevice(clazz, host)"
        result =  tu.addDevice(clazz, host)

      }else if(clazz && device){
      println "        result = tu.addDevice(clazz, device)"
        result = tu.addQualifiedDevice(clazz, device)
                
      }else {
      println "        result = tu.addDevice(clazz)"
        result =  tu.addDevice(clazz)
      }
    }
    
    // Check if it's a subproxy
    if (builder.current instanceof Device){
      println "Add ${result.name} as subproxy of ${builder.current.name}"
      (builder.current as Device).subproxies.add(result)
    }
    
    // Register variable context
    builder.setVariable(name,result)
    println "Register $name as attribute of $builder (${builder."$name"}"
println "+++++++++++++++++++++++"
    return result;
  }

  //Gives the factory the ability to process the attributes as it may see fit with the option of stopping the builder to process them itself (by returning true).
  /**/
  boolean onHandleNodeAttributes( FactoryBuilderSupport builder, Object node, Map attributes ){
      return false
  }
   

  /*
  void setParent( FactoryBuilderSupport builder, Object parent, Object child ){
      
  }
   */

  //allows the factory to setup parent/child relationships.
  /*
  void setChild( FactoryBuilderSupport builder, Object parent, Object child ){
      
  }
   */

  //Lets the builder know if the node allows for further nodes to be nested on the current node.
  boolean isLeaf(){
    return false;   
  }
  

  //Is the last method called from the factories perspective, it will let you handle any cleanup the node may require.
  /*
  void onNodeCompleted( FactoryBuilderSupport builder, Object parent, Object node ){
      
  }
   */
}

abstract class VoidFactory extends AbstractFactory{
  AbstractFactory defFactory
  AbstractFactory otherFactory = new MapFactory()
  
  abstract def getKey()
  
  //Responsible for creating the object that responds to the node 'name' and its called during builder.createNode
  Object newInstance( FactoryBuilderSupport builder, Object name, Object value, Map attributes ) throws InstantiationException, IllegalAccessException{

      saveDefault(builder)
    
    return name;
  }
  
  //Is the last method called from the factories perspective, it will let you handle any cleanup the node may require.
  void onNodeCompleted( FactoryBuilderSupport builder, Object parent, Object node ){
      loadDefault(builder)
  }
  //Lets the builder know if the node allows for further nodes to be nested on the current node.
  boolean isLeaf(){
    return false;   
  }


  def saveDefault(FactoryBuilderSupport builder){
    defFactory=builder.defaultt
    builder.defaultt = this.otherFactory
    println "save $defFactory replaced by $otherFactory"
  }
  def loadDefault(FactoryBuilderSupport builder){
    builder.defaultt = defFactory
    println "reload $defFactory"
  }
  

}

class TangoPropertiesFactory extends VoidFactory{
  
  def getKey(){"properties"}
  
    //Responsible for creating the object that responds to the node 'name' and its called during builder.createNode
  Object newInstance( FactoryBuilderSupport builder, Object name, Object value, Map attributes ) throws InstantiationException, IllegalAccessException{
    super.newInstance(builder, name, value, attributes)
    println "Properties - Current Node = ${builder.current} ${builder.current.class}"
    return builder.current.properties
  }

  //allows the factory to setup parent/child relationships.
  void setChild( FactoryBuilderSupport builder, Object parent, Object child ){
    
    parent.add(new Property(child["name"], child["value"]))
  }
   

}

class TangoUnitStarterFactory extends VoidFactory{
    
  def getKey(){"starters"}
  
  //Responsible for creating the object that responds to the node 'name' and its called during builder.createNode
  Object newInstance( FactoryBuilderSupport builder, Object name, Object value, Map attributes ) throws InstantiationException, IllegalAccessException{
    super.newInstance(builder, name, value, attributes)
    return Factories.starters
  }
   //allows the factory to setup parent/child relationships.
  void setChild( FactoryBuilderSupport builder, Object parent, Object child ){
      Factories.addStarter(child["name"],Starter.Platform.valueOf(child["value"]))
  }



}

class TangoUnitProgramFactory extends VoidFactory{
    
  def getKey(){"programs"}
  
  //Responsible for creating the object that responds to the node 'name' and its called during builder.createNode
  Object newInstance( FactoryBuilderSupport builder, Object name, Object value, Map attributes ) throws InstantiationException, IllegalAccessException{
    super.newInstance(builder, name, value, attributes)
    return Factories.programs
  }
   //allows the factory to setup parent/child relationships.
  void setChild( FactoryBuilderSupport builder, Object parent, Object child ){
      Factories.programs.put(child["name"],child["value"])
  }

}

/**
 * just return a map from name and value
 */
class MapFactory extends AbstractFactory{
    
  //Responsible for creating the object that responds to the node 'name' and its called during builder.createNode
  Object newInstance( FactoryBuilderSupport builder, Object name, Object value, Map attributes ) throws InstantiationException, IllegalAccessException{
    def result = ["name":name, "value":value, "attributes":attributes]
    println """MapFactory : "name":$name, "value":$value, "attributes":$attributes"""
    return result
  }

    //Lets the builder know if the node allows for further nodes to be nested on the current node.
  boolean isLeaf(){
    
    return true;   
  }

  

}