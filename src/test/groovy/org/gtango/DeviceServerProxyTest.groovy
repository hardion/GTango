/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.gtango

import spock.lang.*

class DeviceServerProxyTest extends Specification {
  
  def "Does spock can replace my favorite test framework?"() {
    //    System.setProperty("TANGO_HOST","192.168.0.16:20001")
    setup:
    def client = Mock(DeviceServerProxy)
    client.boolean_scalar >> false
    
    expect:
    client."$attribute" == value

    when :
    client."$attribute" = value // TODO : search the good keyword
    
    then:
    attribute       | value
    boolean_scalar  | true
    long_scalar     | 13
  }
  
  def "Test Command"() {
    //    System.setProperty("TANGO_HOST","192.168.0.16:20001")
    setup:
    def client = Mock(DeviceServerProxy)

    expect:
    client."$command"(value) == value

    where:
    command       | value
    DevDouble     | 3.14
    DevString     | "My String is short"
  }
}

