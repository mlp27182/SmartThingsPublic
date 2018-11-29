/**
 *Infinitive interface to Carrier thermostat
 *
 Connects to Infinitive
 https://github.com/acd/infinitive
 
 
 * Based on:
 *Filtrete 3M-50 WiFi Thermostat.
 *
 *  For more information, please visit:
 *  <https://github.com/statusbits/smartthings/tree/master/RadioThermostat/>
 * https://raw.githubusercontent.com/statusbits/smartthings/master/devicetypes/statusbits/radio-thermostat.src/radio-thermostat.groovy
 
 *  --------------------------------------------------------------------------
 *
 *  Copyright © 2014 Statusbits.com
 *
 *  This program is free software: you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the Free
 *  Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 *  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 *  for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  --------------------------------------------------------------------------
 *
 *  Version 2.0.1 (12/20/2016)
 */

import groovy.json.JsonSlurper

preferences {
    input("confIpAddr", "string", title:"Thermostat IP Address",
        required:true, displayDuringSetup:true)

    // FIXME: Android client does not accept "defaultValue" attribute!
    //input("confTcpPort", "number", title:"Thermostat TCP Port",
    //    defaultValue:80, required:true, displayDuringSetup:true)
    //input("pollingInterval", "number", title:"Polling interval in minutes (1 - 59)",
    //    defaultValue:5, required:true, displayDuringSetup:true)
    input("confTcpPort", "number", title:"Thermostat TCP Port (default: 80)",
        required:true, displayDuringSetup:true)

    input("pollingInterval", "number", title:"Polling interval in minutes (1 - 59)",
        required:true, displayDuringSetup:true)
}

metadata {
    definition (name:"Carrier Infinitive", namespace:"Infinitive", author:"mlp27182") {
        capability "Thermostat"
		capability "Temperature Measurement"
		capability "Sensor"
		capability "Refresh"
		capability "Relative Humidity Measurement"
        capability "Polling"

        // Custom attributes
        attribute "fanState", "string"      // Fan operating state. Values: "on", "off"
        attribute "hold", "string"          // Target temperature Hold status. Values: "on", "off"
        attribute "connection", "string"    // Connection status string
        attribute "outsideTemperature", "number"	//Outside temperature reading

        // Custom commands
        command "temperatureUp"
        command "temperatureDown"
        command "holdOn"
        command "holdOff"
        command "switchMode"
		command "switchFanMode"
    }

    tiles(scale:2) {
        multiAttributeTile(name:"thermostat", type:"thermostat", width:6, height:4) {
		    tileAttribute("device.temperature", key:"PRIMARY_CONTROL") {
			    attributeState("default", label:'${currentValue}°', unit:"dF", defaultState:true,
                    backgroundColors:[
                        [value:10, color:"#153591"],
                        [value:15, color:"#1e9cbb"],
                        [value:18, color:"#90d2a7"],
                        [value:21, color:"#44b621"],
                        [value:24, color:"#f1d801"],
                        [value:27, color:"#d04e00"],
                        [value:30, color:"#bc2323"],
                        [value:31, color:"#153591"],
                        [value:44, color:"#1e9cbb"],
                        [value:59, color:"#90d2a7"],
                        [value:74, color:"#44b621"],
                        [value:84, color:"#f1d801"],
                        [value:95, color:"#d04e00"],
                        [value:96, color:"#bc2323"]
                    ]
                )
	        }
			tileAttribute("device.temperature", key:"VALUE_CONTROL") {
				attributeState("VALUE_UP", action:"temperatureUp")
				attributeState("VALUE_DOWN", action:"temperatureDown")
			}
/*			tileAttribute("device.thermostatOperatingState", key:"OPERATING_STATE") {
				attributeState("idle", backgroundColor:"#44b621", defaultState:true)
				attributeState("heating", backgroundColor:"#ea5462")
				attributeState("cooling", backgroundColor:"#269bd2")
			}
            */
			tileAttribute("device.thermostatMode", key:"THERMOSTAT_MODE") {
				attributeState("off", label:'${name}', defaultState:true)
				attributeState("heat", label:'${name}')
				attributeState("cool", label:'${name}')
				attributeState("auto", label:'${name}')
			}
			tileAttribute("device.heatingSetpoint", key:"HEATING_SETPOINT") {
				attributeState("heatingSetpoint", label:'${currentValue}', unit:" dF", defaultState:true)
			}
			tileAttribute("device.coolingSetpoint", key:"COOLING_SETPOINT") {
				attributeState("heatingSetpoint", label:'${currentValue}', unit:" dF", defaultState:true)
			}
            tileAttribute("device.humidity", key: "SECONDARY_CONTROL") {
				attributeState "humidity", label:'RH: ${currentValue}%', unit:" \\%", icon:"st.Weather.weather12"
			}
 //           tileAttribute("device.outsideTemperature", key: "SECONDARY_CONTROL") {
//				attributeState "outsideTemperature", label:'${currentValue}%', unit:" dF", icon:"st.Weather.weather12"
//			}
            
        }

        valueTile("outsideTemperature", "device.outsideTemperature", width:2, height:1, decoration:"flat") {
            state "outsideTemperature", label:'Outside: ${currentValue}°', unit:"dF",
 
 					backgroundColors:[
                    [value:10, color:"#153591"],
                    [value:15, color:"#1e9cbb"],
                    [value:18, color:"#90d2a7"],
                    [value:21, color:"#44b621"],
                    [value:24, color:"#f1d801"],
                    [value:27, color:"#d04e00"],
                    [value:30, color:"#bc2323"],
                    [value:31, color:"#153591"],
                    [value:44, color:"#1e9cbb"],
                    [value:59, color:"#90d2a7"],
                    [value:74, color:"#44b621"],
                    [value:84, color:"#f1d801"],
                    [value:95, color:"#d04e00"],
                    [value:96, color:"#bc2323"]
                ]
        }


  /*      standardTile("modeHeat", "device.thermostatMode", width:2, height:2) {
            state "default", label:'', icon:"st.thermostat.heat", backgroundColor:"#FFFFFF", action:"thermostat.heat", defaultState:true
            state "heat", label:'', icon:"st.thermostat.heat", backgroundColor:"#FFCC99", action:"thermostat.off"
        }

        standardTile("modeCool", "device.thermostatMode", width:2, height:2) {
            state "default", label:'', icon:"st.thermostat.cool", backgroundColor:"#FFFFFF", action:"thermostat.cool", defaultState:true
            state "cool", label:'', icon:"st.thermostat.cool", backgroundColor:"#99CCFF", action:"thermostat.off"
        }

        standardTile("modeAuto", "device.thermostatMode", width:2, height:2) {
            state "default", label:'', icon:"st.thermostat.auto", backgroundColor:"#FFFFFF", action:"thermostat.auto", defaultState:true
            state "auto", label:'', icon:"st.thermostat.auto", backgroundColor:"#99FF99", action:"thermostat.off"
        }
*/
		standardTile("mode", "device.thermostatMode", width:2, height:2, inactiveLabel: false, decoration: "flat") {
			state "off", action:"switchMode", nextState: "updating", icon: "st.thermostat.heating-cooling-off"
			state "heat", action:"switchMode",  nextState: "updating", icon: "st.thermostat.heat"
			state "cool", action:"switchMode",  nextState: "updating", icon: "st.thermostat.cool"
			state "auto", action:"switchMode",  nextState: "updating", icon: "st.thermostat.auto"
		//	state "emergency heat", action:"switchMode", nextState: "updating", icon: "st.thermostat.emergency-heat"
			state "updating", label:"Updating...", icon: "st.secondary.secondary"
		}





		standardTile("fanMode", "device.thermostatFanMode", width:2, height:2, inactiveLabel: false) {
			state "low", label:'Low', action:"switchFanMode", nextState: "updating", icon: "st.samsung.da.RAC_4line_02_ic_fan"
            state "med", label:'Med', action:"switchFanMode", nextState: "updating", icon: "st.samsung.da.RAC_4line_02_ic_fan"
			state "high", label:'High', action:"switchFanMode", nextState: "updating", icon: "st.samsung.da.RAC_4line_02_ic_fan"
            state "auto", action:"switchFanMode", nextState: "updating", icon: "st.thermostat.fan-auto"
			state "updating", label:"Updating...", icon: "st.secondary.secondary"
		}
        

        standardTile("hold", "device.hold", width:2, height:2) {
            state "off", label:'Hold', icon:"st.Weather.weather2", backgroundColor:"#FFFFFF", action:"holdOn", defaultState:true
            state "on", label:'Hold', icon:"st.Weather.weather2", backgroundColor:"#A4FCA6", action:"holdOff"
        }

        standardTile("refresh", "device.connection", width:1, height:1, decoration:"flat") {
        //standardTile("refresh", "device.connection", width:2, height:2) {
            state "default", icon:"st.secondary.refresh", backgroundColor:"#FFFFFF", action:"refresh.refresh", defaultState:true
            state "connected", icon:"st.secondary.refresh", backgroundColor:"#44b621", action:"refresh.refresh"
            state "disconnected", icon:"st.secondary.refresh", backgroundColor:"#ea5462", action:"refresh.refresh"
        }

		standardTile("spacer", "spacer" , width:3, height:1, decoration:"flat"){
         state "default", label:''
        }


//Current temperature, displayed in device list
        valueTile("temperature", "device.temperature", width:6, height:1, icon: "st.Home.home1", canChangeIcon: true, canChangeBackground: true) {
            state "temperature", label:'${currentValue}°', unit:"dF",
                backgroundColors:[
                    [value:10, color:"#153591"],
                    [value:15, color:"#1e9cbb"],
                    [value:18, color:"#90d2a7"],
                    [value:21, color:"#44b621"],
                    [value:24, color:"#f1d801"],
                    [value:27, color:"#d04e00"],
                    [value:30, color:"#bc2323"],
                    [value:31, color:"#153591"],
                    [value:44, color:"#1e9cbb"],
                    [value:59, color:"#90d2a7"],
                    [value:74, color:"#44b621"],
                    [value:84, color:"#f1d801"],
                    [value:95, color:"#d04e00"],
                    [value:96, color:"#bc2323"]
                ]
        }

        main("temperature")
        
        
        details([
            "thermostat",
            "outsideTemperature", "spacer", "refresh",
//            "modeHeat", "modeCool", "modeAuto",
            "mode", "fanMode", "hold", 
           
        ])
    }

    simulator {
        status "Temperature 72.0":      "simulator:true, temp:72.00"
        status "Cooling Setpoint 76.0": "simulator:true, t_cool:76.00"
        status "Heating Setpoint 68.0": "simulator:true, t_cool:68.00"
        status "Thermostat Mode Off":   "simulator:true, tmode:0"
        status "Thermostat Mode Heat":  "simulator:true, tmode:1"
        status "Thermostat Mode Cool":  "simulator:true, tmode:2"
        status "Thermostat Mode Auto":  "simulator:true, tmode:3"
        status "Fan Mode Auto":         "simulator:true, fmode:0"
        status "Fan Mode Circulate":    "simulator:true, fmode:1"
        status "Fan Mode On":           "simulator:true, fmode:2"
//        status "State Off":             "simulator:true, tstate:0"
//        status "State Heat":            "simulator:true, tstate:1"
//        status "State Cool":            "simulator:true, tstate:2"
        status "Fan State Off":         "simulator:true, fstate:0"
        status "Fan State On":          "simulator:true, fstate:1"
        status "Hold Disabled":         "simulator:true, hold:0"
        status "Hold Enabled":          "simulator:true, hold:1"
    }
}

def installed() {
    //log.debug "installed()"

    printTitle()

    // Initialize attributes to default values (Issue #18)
    sendEvent([name:'temperature', value:'70', displayed:false])
    sendEvent([name:'outsideTemperature', value:'60', displayed:false])
    sendEvent([name: "humidity", value:'50', displayed:false])
    sendEvent([name:'heatingSetpoint', value:'70', displayed:false])
    sendEvent([name:'coolingSetpoint', value:'72', displayed:false])
    sendEvent([name:'thermostatMode', value:'off', displayed:false])
    sendEvent([name:'thermostatFanMode', value:'auto', displayed:false])
 //   sendEvent([name:'thermostatOperatingState', value:'idle', displayed:false])
    sendEvent([name:'fanState', value:'off', displayed:false])
    sendEvent([name:'hold', value:'off', displayed:false])
    sendEvent([name:'connection', value:'disconnected', displayed:false])
}

def updated() {
	//log.debug "updated with settings: ${settings}"

    printTitle()
    unschedule()

    if (device.currentValue('connection') == null) {
        sendEvent([name:'connection', value:'disconnected', displayed:false])
    }

    if (!settings.confIpAddr) {
	    log.warn "IP address is not set!"
        return
    }

    def port = settings.confTcpPort
    if (!port) {
	    log.warn "Using default TCP port 8080!"
        port = 8080
    }

    def dni = createDNI(settings.confIpAddr, port)
    device.deviceNetworkId = dni
    state.dni = dni
    state.hostAddress = "${settings.confIpAddr}:${settings.confTcpPort}"
    state.requestTime = 0
    state.responseTime = 0

    startPollingTask()
   // STATE()
}

def pollingTask() {
    //log.debug "pollingTask()"

    state.lastPoll = now()

    // Check connection status
    def requestTime = state.requestTime ?: 0
    def responseTime = state.responseTime ?: 0
  
    if (requestTime && (requestTime - responseTime) > 5000) {
        log.warn "No connection!"
        sendEvent([
            name:           'connection',
            value:          'disconnected',
            isStateChange:  true,
            displayed:      true
        ])
    }

    def updated = state.updated ?: 0
    if ((now() - updated) > 10000) {
        sendHubCommand(apiGet("/api/zone/1/config"))
    }
}

def parse(String message) {
    //log.debug "parse(${message})"

    def msg = stringToMap(message)
    if (msg.headers) {
        // parse HTTP response headers
        def headers = new String(msg.headers.decodeBase64())

        def parsedHeaders = parseHttpHeaders(headers)
        //log.debug "parsedHeaders: ${parsedHeaders}"
        if (parsedHeaders.status != 200) {
        	log.error "Server error: ${parsedHeaders}"
            return null
        }

        // parse HTTP response body
       if (!msg.body) {
           log.error "HTTP response has no body"
            return null
       }


        
        def body = new String(msg.body.decodeBase64())      
        def slurper = new JsonSlurper()
        def tstat = slurper.parseText(body)
        return parseTstatData(tstat)
    } else if (msg.containsKey("simulator")) {
        // simulator input
        return parseTstatData(msg)
    }

    return null
}

// thermostat.setThermostatMode
def setThermostatMode(mode) {
    //log.debug "setThermostatMode(${mode})"

    switch (mode) {
    case "off":             return off()
    case "heat":            return heat()
    case "cool":            return cool()
    case "auto":            return auto()
    case "emergency heat":  return emergencyHeat()
    }

    log.error "Invalid thermostat mode: \'${mode}\'"
    return null
}

// thermostat.off
def off() {
    //log.debug "off()"

    if (device.currentValue("thermostatMode") == "off") {
        return null
    }

    log.info "Setting thermostat mode to 'off'"
    sendEvent([name:"thermostatMode", value:"off"])

    return writeTstatValue('mode', "off")
}

// thermostat.heat
def heat() {
    //log.debug "heat()"

    if (device.currentValue("thermostatMode") == "heat") {
        return null
    }

    log.info "Setting thermostat mode to 'heat'"
    sendEvent([name:"thermostatMode", value:"heat"])

    return writeTstatValue('mode', 'heat')
}

// thermostat.cool
def cool() {
    //log.debug "cool()"

    if (device.currentValue("thermostatMode") == "cool") {
        return null
    }

    log.info "Setting thermostat mode to 'cool'"
    sendEvent([name:"thermostatMode", value:"cool"])

    return writeTstatValue('mode', "cool")
}

// thermostat.auto
def auto() {
    //log.debug "auto()"

    if (device.currentValue("thermostatMode") == "auto") {
        return null
    }

    log.info "Setting thermostat mode to 'auto'"
    sendEvent([name:"thermostatMode", value:"auto"])

    return writeTstatValue('mode', "auto")
}

// thermostat.emergencyHeat
def emergencyHeat() {
    //log.debug "emergencyHeat()"
    log.warn "'emergency heat' mode is not supported"
    return null
}

// thermostat.setThermostatFanMode
def setThermostatFanMode(fanMode) {
    log.debug "setThermostatFanMode(${fanMode})"

    switch (fanMode) {
    case "auto":        return fanAuto()
    case "low":   return fanLow()
    case "med":   return fanMed()
    case "high":          return fanOn()
    }

    log.error "Invalid fan mode: \'${fanMode}\'"
    return null
}

// thermostat.fanAuto
def fanAuto() {
    //log.debug "fanAuto()"

    if (device.currentValue("thermostatFanMode") == "auto") {
        return null
    }

    log.info "Setting fan mode to 'auto'"
    sendEvent([name:"thermostatFanMode", value:"auto"])

    return writeTstatValue('fanMode', 'auto')
}

// thermostat.fanLow
def fanLow() {

    log.info "Setting fan mode to 'low'"
    sendEvent([name:"thermostatFanMode", value:"low"])

    return writeTstatValue('fanMode', 'low')
}

// thermostat.fanMed
def fanMed() {

    log.info "Setting fan mode to 'medium'"
    sendEvent([name:"thermostatFanMode", value:"med"])

    return writeTstatValue('fanMode', 'med')
}


// thermostat.fanOn
def fanOn() {
    //log.debug "fanOn()"

    if (device.currentValue("thermostatFanMode") == "on") {
        return null
    }

    log.info "Setting fan mode to 'high'"
    sendEvent([name:"thermostatFanMode", value:"high"])

    return writeTstatValue('fanMode', 'high')
}

// switch between thermostat speeds
//def setFanMode(fanSpeed) {
    //log.debug "fanOn()"

//    if (device.currentValue("thermostatFanMode") == "on") {
//       return null
//    }

//    log.info "Setting fan mode to ${fanSpeed}"
//    sendEvent([name:"thermostatFanMode", value:fanSpeed])

//    return writeTstatValue('fanMode', fanSpeed)
//}


// thermostat.setHeatingSetpoint
def setHeatingSetpoint(temp) {
    //log.debug "setHeatingSetpoint(${temp})"

    double minT = 36.0
    double maxT = 94.0
    def scale = getTemperatureScale()
    double t = (scale == "C") ? temperatureCtoF(temp) : temp

    t = t.round()
    if (t < minT) {
        log.warn "Cannot set heating target below ${minT} °F."
        return null
    } else if (t > maxT) {
        log.warn "Cannot set heating target above ${maxT} °F."
        return null
    }

    log.info "Setting heating setpoint to ${t} °F"

    def ev = [
        name:   "heatingSetpoint",
        value:  (scale == "C") ? temperatureFtoC(t) : t,
        unit:   scale,
    ]

    sendEvent(ev)
	//Infinitive JSON API does not accept decimal input
    return writeTstatValue('heatSetpoint', t.round())
}

// thermostat.setCoolingSetpoint
def setCoolingSetpoint(temp) {
    //log.debug "setCoolingSetpoint(${temp})"

    double minT = 36.0
    double maxT = 94.0
    def scale = getTemperatureScale()
    double t = (scale == "C")
    t = t.round()
    if (t < minT) {
        log.warn "Cannot set cooling target below ${minT} °F."
        return null
    } else if (t > maxT) {
        log.warn "Cannot set cooling target above ${maxT} °F."
        return null
    }

    log.info "Setting cooling setpoint to ${t} °F"

    def ev = [
        name:   "coolingSetpoint",
        value:  (scale == "C") ? temperatureFtoC(t) : t,
        unit:   scale,
    ]

    sendEvent(ev)

    return writeTstatValue('coolSetpoint', t.round())
}

// Custom command
def temperatureUp() {
    log.debug "temperatureUp()"

    def step = (getTemperatureScale() == "C") ? 0.5 : 1
    def mode = device.currentValue("thermostatMode")
    if (mode == "heat") {
        def t = device.currentValue("heatingSetpoint")?.toFloat()
        if (!t) {
            log.error "Cannot get current heating setpoint."
            return null
        }
        return setHeatingSetpoint(t + step)
    } else if (mode == "cool") {
        def t = device.currentValue("coolingSetpoint")?.toFloat()
        if (!t) {
            log.error "Cannot get current cooling setpoint."
            return null
        } 
        return setCoolingSetpoint(t + step)
    } else {
        log.warn "Cannot change temperature while in '${mode}' mode."
        return null
    }
}

// Custom command
def temperatureDown() {
    //log.debug "temperatureDown()"

    def step = (getTemperatureScale() == "C") ? 0.5 : 1
    def mode = device.currentValue("thermostatMode")
    if (mode == "heat") {
        def t = device.currentValue("heatingSetpoint")?.toFloat()
        if (!t) {
            log.error "Cannot get current heating setpoint."
            return null
        }
 
        return setHeatingSetpoint(t - step)
    } else if (mode == "cool") {
    	        def t = device.currentValue("coolingSetpoint")?.toFloat()
        if (!t) {
            log.error "Cannot get current cooling setpoint."
            return null
        }
 
        return setCoolingSetpoint(t - step)
    } else {
        log.warn "Cannot change temperature while in '${mode}' mode."
        return null
    }
}

// Custom command
def holdOn() {
    //log.debug "holdOn()"

    if (device.currentValue("hold") == "on") {
        return null
    }

    log.info "Setting temperature hold to 'on'"
    sendEvent([name:"hold", value:"on"])

    return writeTstatValue("hold", "true")
}

// Custom command
def holdOff() {
    //log.debug "holdOff()"

    if (device.currentValue("hold") == "off") {
        return null
    }

    log.info "Setting temperature hold to 'off'"
    sendEvent([name:"hold", value:"off"])

    return writeTstatValue("hold", "false")
}

// polling.poll 
def poll() {
    //log.debug "poll()"
    return refresh()
}

// refresh.refresh
def refresh() {
    //log.debug "refresh()"
    //STATE()

    if (!state.dni) {
	    log.warn "DNI is not set! Please enter device IP address and port in settings."
        sendEvent([
            name:           'connection',
            value:          'disconnected',
            isStateChange:  true,
            displayed:      false
        ])

        return null
    }

    def interval = getPollingInterval() * 60
    def elapsed =  (now() - state.lastPoll) / 1000
    if (elapsed > (interval + 300)) {
        log.warn "Restarting polling task..."
        unschedule()
        startPollingTask()
    }

    return apiGet("/tstat")
}

private getPollingInterval() {
    def minutes = settings.pollingInterval?.toInteger()
    if (!minutes) {
	    log.warn "Using default polling interval: 5!"
        minutes = 5
    } else if (minutes < 1) {
        minutes = 1
    } else if (minutes > 59) {
        minutes = 59
    }

    return minutes
}

private startPollingTask() {
    //log.debug "startPollingTask()"

    pollingTask()

    Random rand = new Random(now())
    def seconds = rand.nextInt(60)
    def sched = "${seconds} 0/${getPollingInterval()} * * * ?"

    //log.debug "Scheduling polling task with \"${sched}\""
    schedule(sched, pollingTask)
}

def switchMode() {
	log.debug "Switching to mode \"${thermostatMode}\""
	def currentMode = device.currentValue("thermostatMode")
	def modeOrder =     ["off", "heat", "cool", "auto" ]
	if (modeOrder) {
		def next = { modeOrder[modeOrder.indexOf(it) + 1] ?: modeOrder[0] }
		def nextMode = "\"" +next(currentMode) + "\""
        log.info "Setting thermostat mode to ${nextMode}"
        
    sendEvent([name:"thermostatMode", value:next(currentMode)])

    return writeTstatValue('mode', nextMode)
	} 
    
    
    else {
		log.warn "supportedThermostatModes not defined"
	}
}

def switchFanMode() {
	log.debug "Switching fan speed to \"${thermostatFanMode}\""
	def currentFanMode = device.currentValue("thermostatFanMode")
    	// use hard coded values
	
	def fanModeOrder = ["low", "med", "high","auto"]
    
	def next = { fanModeOrder[fanModeOrder.indexOf(it) + 1] ?: fanModeOrder[0] }
   
//	setFanMode(next(currentFanMode))
        log.info "Setting fan mode to ${next(currentFanMode)}"
    sendEvent([name:"thermostatFanMode", value:next(currentFanMode)])

    return writeTstatValue('fanMode', "\"" + next(currentFanMode) + "\"")
}



def modes() {
	// use hard coded values
    ["off", "heat", "cool", "auto" ]
}

/*def fanModes() {
	// use hard coded values
	["low", "med", "high","auto"]
}

*/

private apiGet(String path) {
    //log.debug "apiGet(${path})"

    if (!updateDNI()) {
        return null
    }

    state.requestTime = now()

    def headers = [
        HOST:       state.hostAddress,
        Accept:     "*/*"
    ]

    def httpRequest = [
        method:     'GET',
        path:       path,
        headers:    headers
    ]

    return new physicalgraph.device.HubAction(httpRequest)
}

private apiPut(String path, data) {
    log.debug "apiPut(${path}, ${data})"

    if (!updateDNI()) {
        return null
    }

    state.requestTime = now()

    def headers = [
        HOST:       state.hostAddress,
        "Content-Type": "application/json",
       Accept:     "*/*"
    ]

    def httpRequest = [
        method:     "PUT",
        path:       path,
        headers:    headers,
        body:       data
    ]
	log.debug "httpRequest(${httpRequest})"
    return new physicalgraph.device.HubAction(httpRequest)
}

private def writeTstatValue(name, value) {
    log.debug "writeTstatValue(${name}, ${value})"

    def json = "{\"${name}\": ${value}}"
    def hubActions = [
        apiPut("/api/zone/1/config", json),
        delayHubAction(2500),
        apiGet("/api/zone/1/config")
    ]

    return hubActions
}

private def delayHubAction(ms) {
    return new physicalgraph.device.HubAction("delay ${ms}")
}

private parseHttpHeaders(String headers) {
    def lines = headers.readLines()
    def status = lines.remove(0).split()

    def result = [
        protocol:   status[0],
        status:     status[1].toInteger(),
        reason:     status[2]
    ]

    return result
}

private def parseTstatData(Map tstat) {
    log.trace "tstat data: ${tstat}"

    state.responseTime = now()

    def events = []
    if (tstat.containsKey("error_msg")) {
        log.error "Thermostat error: ${tstat.error_msg}"
        return null
    }

    if (tstat.containsKey("success")) {
        // this is POST response - ignore
       // return null
    }

    if (tstat.containsKey("currentTemp")) {
        events << createEvent([
            name:   "temperature",
            value:  scaleTemperature(tstat.currentTemp.toFloat()),
            unit:   getTemperatureScale(),
        ])
    }
 
     if (tstat.containsKey("outdoorTemp")) {
        events << createEvent([
            name:   "outsideTemperature",
            value:  scaleTemperature(tstat.outdoorTemp.toFloat()),
            unit:   getTemperatureScale(),
        ])
    }
 
        if (tstat.containsKey("currentHumidity")) {
        events << createEvent([
            name:   "humidity",
            value:  tstat.currentHumidity.toFloat(),
           // unit:   getTemperatureScale(),
        ])
    }

    if (tstat.containsKey("coolSetpoint")) {
        events << createEvent([
            name:   "coolingSetpoint",
            value:  scaleTemperature(tstat.coolSetpoint.toFloat()),
            unit:   getTemperatureScale(),
        ])
    }

    if (tstat.containsKey("heatSetpoint")) {
        events << createEvent([
            name:   "heatingSetpoint",
            value:  scaleTemperature(tstat.heatSetpoint.toFloat()),
            unit:   getTemperatureScale(),
        ])
    }

 /*   if (tstat.containsKey("mode")) {
        events << createEvent([
            name:   "thermostatOperatingState",
            value:  parseThermostatState(tstat.tstate)
        ])
    }

    if (tstat.containsKey("fanMode")) {
        events << createEvent([
            name:   "fanState",
            value:  parseFanState(tstat.fanMode)
        ])
    }
*/
    if (tstat.containsKey("mode")) {
        events << createEvent([
            name:   "thermostatMode",
           // value:  parseThermostatMode(tstat.mode)
           value: tstat.mode
        ])
    }

    if (tstat.containsKey("fanMode")) {
        events << createEvent([
            name:   "thermostatFanMode",
         //   value:  parseFanMode(tstat.fanMode)
         value: tstat.fanMode
        ])
    }

    if (tstat.containsKey("hold")) {
        events << createEvent([
            name:   "hold",
            value:  parseThermostatHold(tstat.hold)
        ])
    }

    events << createEvent([
        name:           'connection',
        value:          'connected',
        isStateChange:  true,
        displayed:      false
    ])

    state.updated = now()

    //log.debug "events: ${events}"
    return events
}

private def parseThermostatState(val) {
    def values = [
        "idle",     // 0
        "heating",  // 1
        "cooling"   // 2
    ]

    return values[val.toInteger()]
}

private def parseFanState(val) {
    def values = [
        "auto",      // 0
        "low",        // 1
        "med",      // 2
        "high"      // 3
    ]

    return values[val.toInteger()]
}

private def parseThermostatMode(val) {
    def values = [
        "off",      // 0
        "heat",     // 1
        "cool",     // 2
        "auto"      // 3
            ]

    return values[val.toInteger()]
}

private def parseFanMode(val) {
    def values = [
        "auto",    // 0
        "low",     // 1 
        "med",     // 2
        "high"	   //3
    ]

    return values[val.toInteger()]
}

private def parseThermostatHold(val) {
     if( val ){
     	return "on"
     }
     else {
    	 return "off"
	}
}

private def scaleTemperature(Double temp) {
    if (getTemperatureScale() == "C") {
        return temperatureFtoC(temp)
    }

    return temp.round(1)
}

private def temperatureCtoF(Double tempC) {
    Double t = (tempC * 1.8) + 32
    return t.round(1)
}

private def temperatureFtoC(Double tempF) {
    Double t = (tempF - 32) / 1.8
    return t.round(1)
}

private String createDNI(ipaddr, port) {
    //log.debug "createDNI(${ipaddr}, ${port})"

    def hexIp = ipaddr.tokenize('.').collect {
        String.format('%02X', it.toInteger())
    }.join()

    def hexPort = String.format('%04X', port.toInteger())
 
    return "${hexIp}:${hexPort}"
}

private updateDNI() {
    if (!state.dni) {
	    log.warn "DNI is not set! Please enter device IP address and port in settings."
        return false
    }
 
    if (state.dni != device.deviceNetworkId) {
	    log.warn "Invalid DNI: ${device.deviceNetworkId}!"
        device.deviceNetworkId = state.dni
    }

    return true
}

private def printTitle() {
    log.info "Carrier Infinitive Interface ${textVersion()}. ${textCopyright()}"
}

private def textVersion() {
    return "Version 0.2 (11/28/2018)"
}

private def textCopyright() {
    return ""
}

private def STATE() {
    log.trace "state: ${state}"
    log.trace "deviceNetworkId: ${device.deviceNetworkId}"
    log.trace "temperature: ${device.currentValue("temperature")}"
    log.trace "heatingSetpoint: ${device.currentValue("heatingSetpoint")}"
    log.trace "coolingSetpoint: ${device.currentValue("coolingSetpoint")}"
 //   log.trace "thermostatOperatingState: ${device.currentValue("thermostatOperatingState")}"
    log.trace "thermostatMode: ${device.currentValue("thermostatMode")}"
    log.trace "thermostatFanMode: ${device.currentValue("thermostatFanMode")}"
 //   log.trace "fanState: ${device.currentValue("fanState")}"
    log.trace "hold: ${device.currentValue("hold")}"
    log.trace "connection: ${device.currentValue("connection")}"
}