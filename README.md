# Turmeric

Deferred execution, with dependencies. For Clojure.

Turmeric is a small tool for partially applying functions with named arguments,
in the form of maps. It provides a macro, `defer`, that accepts a symbol as a name,
the required named arguments and the form to be executed once all arguments
have been applied. It also provides a function, `spice`, which accepts only
the required arguments and the form to be executed and returns an anonymous function.

## Installation

Add the following to your project.clj dependencies:
```
[turmeric "1.0.0"]
```

## Usage

Just require it where necessary in the ns macro.
```
(ns your-project.namespace
  (:require [turmeric :as t]))

(t/defer deferred-form [a b] (+ a b))
(def plus-four (deferred-form {:b 4}))
(plus-four {:a 2})
;; => 6
```

Or using the require function.
```
(require '[turmeric :as t])

(let [spiced-function (t/spice '[a] '(fn [b] (+ a b)))]
  ((spiced-function {:a 5}) 3))
;; => 9
```

## Examples

Returns immediately upon receiving all necessary arguments.
```
(defer deferred-db-func [system table flags]
  (let [{db :db} system]
    (some-db-func db table flags)))

(def db-func-with-system
  (deferred-db-func {:system system}))

(db-func-with-system {:table "users", :flags {})
;; returns the result of some-db-func
```

Wrap in an anonymous function to be called later
```
(defer deferred-db-func [system table]
  (let [{db :db} system]
    (fn [flags]
      (some-db-func db table flags))))

(def give-me-flags
  (deffered-db-func {:system system, :table "users"}))
;; returns a function that expects the flags argument

(give-me-flags {:order-by-age true})
;; returns the result of some-db-func
```

## License

Copyright © 2016 Shayden Martin

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
