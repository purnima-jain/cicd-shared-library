package com.purnima.jain

def green(String script) {
    //ansiColor('xterm') {
        echo "\033[1;32m${script}\033[0m"
    //}
    
}