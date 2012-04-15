package org.gtango.example


import org.gtango.client.DeviceServerProxy

def testClient() {
    println 'Test Client'
    System.setProperty("TANGO_HOST","192.168.0.15:20001")
    def client = new DeviceServerProxy("sys/tg_test/1")

    println client.boolean_scalar
    println client.long_scalar
    println client.DevDouble(100.0)
    println client.DevLong(200)
    println client.DevString("My String is short")

}

testClient()