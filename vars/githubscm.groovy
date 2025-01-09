def checkoutIfExists(String repository, String author, String branches, String defaultAuthor, 
                    String defaultBranches, boolean mergeTarget = false, 
                    def credentials = ['token': 'kie-ci1-token', 'usernamePassword': 'kie-ci']) {
                        echo "Inside githubscm -> checkoutIfExists()......"
                        echo "credentials: ${credentials}"
                        echo "credentials['token']: ${credentials['token']}"
                        echo "credentials['usernamePassword']: ${credentials['usernamePassword']}"

                        // assert credentials['token']
                        // assert credentials['usernamePassword']
                    }