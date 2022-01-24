# tic-tac-toe

generated using Luminus version "4.30"

I wrote this small two player tic tac toe game for fun. I figured it would be a good way to get familiar with clojure. There is lots of innovative and interesting things in the luminus framework/template. 

- the request and response type is defined in the headers
- the pipeline style of functional thinking is really good for backend servers because most of them have a couple simple steps. receive data -> format data -> validate data -> fetch response data from db -> format data -> return data. I think this model would allow a configuration based approach where each endpoint is essentially a map of data or functions.

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein run 
To start dev frontend run


    npm install
    npm run watch

## License

Copyright © 2022 FIXME
