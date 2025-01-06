#!/usr/bin/env groovy

def call(String name) {
    echo "name: ${name}" // name: business-application-payments-daily

    def domain

    if(name.contains("payments")) {
        domain = "payments"
    } else {
        domain = "default"
    }
    // Lots of other domains are added here

    return domain
}