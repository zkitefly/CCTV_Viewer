                    function simulate(element, eventName) {
                        var options = extend(defaultOptions, arguments[2] || {});
                        var oEvent, eventType = null;

                        for (var name in eventMatchers) {
                            if (eventMatchers[name].test(eventName)) {
                                eventType = name;
                                break;
                            }
                        }

                        if (!eventType) throw new SyntaxError('Only HTMLEvents and MouseEvents interfaces are supported');

                        if (document.createEvent) {
                            oEvent = document.createEvent(eventType);
                            if (eventType == 'HTMLEvents') {
                                oEvent.initEvent(eventName, options.bubbles, options.cancelable);
                            } else {
                                oEvent.initMouseEvent(eventName, options.bubbles, options.cancelable, document.defaultView, options.button, options.pointerX, options.pointerY, options.pointerX, options.pointerY, options.ctrlKey, options.altKey, options.shiftKey, options.metaKey, options.button, element);
                            }
                            element.dispatchEvent(oEvent);
                        } else {
                            options.clientX = options.pointerX;
                            options.clientY = options.pointerY;
                            var evt = document.createEventObject();
                            oEvent = extend(evt, options);
                            element.fireEvent('on' + eventName, oEvent);
                        }
                        return element;
                    }

                    function extend(destination, source) {
                        for (var property in source) destination[property] = source[property];
                        return destination;
                    }

                    var eventMatchers = {
                        'HTMLEvents': /^(?:load|unload|abort|error|select|change|submit|reset|focus|blur|resize|scroll)$/,
                        'MouseEvents': /^(?:click|dblclick|mouse(?:down|up|over|move|out))$/
                    }
                    var defaultOptions = {
                        pointerX: 0,
                        pointerY: 0,
                        button: 0,
                        ctrlKey: false,
                        altKey: false,
                        shiftKey: false,
                        metaKey: false,
                        bubbles: true,
                        cancelable: true
                    }

                    function triggerMouseEvent (node, eventType) {
                        var clickEvent = document.createEvent ('MouseEvents');
                        clickEvent.initEvent (eventType, true, true);
                        node.dispatchEvent (clickEvent);
                    };

                    function mouseDragStart(node) {
                        console.log("Starting drag...");
                        console.log(node.offsetTop);
                        console.log(node.offsetLeft);
                        triggerMouseEvent(node, "mousedown")
                    }

                    function mouseDragEnd(node){
                        console.log("Ending drag...");
                        const rect = node.getBoundingClientRect();
                        document.querySelector("#epg_right_shift_player").click()
                        simulate(node, "mousemove" , {pointerX: rect.x+20 , pointerY: node.offsetTop})
                        simulate(node, "mouseup" , {pointerX:  rect.x+20, pointerY: node.offsetTop})
                    }
                    function sleep1(time) {
                        time*=1000
                        return new Promise(resolve => {
                            setTimeout(() => {
                                resolve();
                            }, time);
                        });
                    }

                    async function playback(){
                        if(document.querySelector("#play_or_pause_play_player").style.display==="none"){
                            document.querySelector("#play_or_plause_player").click()
                        }
                        await sleep1(3)
                        const targetElement = document.querySelector("#timeshift_pointer_player")
                        mouseDragStart(targetElement);
                        mouseDragEnd(targetElement);
                    };

                    playback()
                    console.log('执行了前进');