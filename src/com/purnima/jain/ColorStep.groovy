package com.purnima.jain

def green(String script) {
    echo "${script}"
    ansiColor('xterm') {
        echo "\033[31;1m ${script} \033[0m"
    }
    
}