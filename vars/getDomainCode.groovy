#!/usr/bin/env groovy

def call(String domain) {

    echo "domain: ${domain}" // domain: payments

    if(domain == "payments") {
        return "PYMT"
    } 
    // Lots of other domains are added here    
}