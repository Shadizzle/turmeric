(ns turmeric.exceptions)

(defn undeclared-binding [undeclared deps binds]
  (ex-info "A binding was passed, but not declared as a dependency."
           {:passed undeclared
            :dependencies deps
            :bindings binds}))

(defn bindings-already-passed [bound-twice deps binds]
  (ex-info "Bindings can not be passed twice."
           {:passed bound-twice
            :dependencies deps
            :bindings binds}))

;; TODO: exception for strings, keys and syms that coerce to the same sym
