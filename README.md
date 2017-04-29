# Turmeric

A tool for defining deferred expressions with named dependencies.

Provides a constructor macro and a def macro: `spice` and `defspice`,
which both return a `DeferredExpression` record.

Also provides two functions for working with this record: `add` and `mix`,
which bind dependencies into and evaluate the body of the `DeferredExpression`.
`add` and `mix` both recieve arguments to be bound, either in a map or as an
alternating series of keys and values, `mix` will attempt to evaluate the
expression after binding in it's arguments however. In the case that all of the
`DeferredExpression`'s dependencies have been provided it will return the result
of the expression. Otherwise it will return a new `DeferredExpression`, with all
provided dependencies irreveribly injected into the expression body and only the
remaining dependencies left to be bound.

## How is this useful?

Clojure's solution to named arguments is "just use a map". Which works fine,
but can get complicated when you want to partially apply only certain keys in
that map. This is because partial application is irreversible and does not
allow for an intermediate state between declaring a bound argument and closing
that binding. Turmeric is an attempt at making functions more divisble and to
be honest I'm not really sure how that might be useful yet.

I plan on experimenting with Turmeric in the future and will update this README
with any useful example I encounter.

## Installation

Add the following to your project.clj dependencies:
```
[turmeric "1.1.0"]
```

## Usage

Just require it where necessary in the ns macro.
```
(ns your-project.namespace
  (:require [turmeric.core :refer [defspice add mix]]))

(defspice deferred-form [a b] (+ a b))

(-> deferred-form
    (add :b 4)
    (add 'a 1)
    (mix "a" 2))
;; => 6
```

Or using the require function.
```
(require '[turmeric.core :as t])

(-> (t/spice [a b] #(+ a b %))
    (mix {:a 3, :b 6})
    (apply [3]))
;; => 12
```

## License

Copyright © 2016 Shayden Martin

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
