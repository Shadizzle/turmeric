(ns turmeric.core
  (:require [clojure.set :refer [subset? intersection]]
            [clojure.string :refer [join]]))

(defn- to-sym [v]
  (-> v name symbol))

(defn- keys-to-syms [m]
  (map to-sym (keys m)))

(defn- binds->vector [bindings]
  (reduce-kv #(conj %1 (to-sym %2) %3) [] bindings))

(defn- throw-undeclared-binding [bind]
  (throw (Exception. (str "A binding for \"" bind "\"" " was passed, "
                          "but was not declared as a dependency"))))

(defn- throw-bindings-already-passed [binds]
  (let [binds-str (join "\", \"" binds)]
    (throw (Exception. (str "Bindings can not be passed twice. "
                            "Caused by: \"" binds-str "\"")))))

;; TODO: exception for strings, keys and syms that coerce to the same sym

(defn- spice* [deps binds fbody]
  (let [deps-set  (set deps)
        binds-set (-> binds keys-to-syms set)]
    (doseq [bind binds-set]
      (if-not (deps-set bind)
        (throw-undeclared-binding bind)))
    (if (= deps-set binds-set)
      `(let ~(binds->vector binds) ~fbody)
      `(fn [bmap#]
        (let [bmap-set#    (-> bmap# keys-to-syms set)
              bound-twice# (seq (intersection bmap-set# (quote ~binds-set)))]
          (if bound-twice#
            (throw-bindings-already-passed bound-twice#)))
        (let [deps#  (quote ~deps)
              binds# (merge bmap# ~binds)
              fbody# (quote ~fbody)]
          (eval ((var spice*) deps# binds# fbody#)))))))

(defmacro spice [deps fbody]
  `(identity ~(spice* deps {} fbody)))

(defmacro defer [fname deps fbody]
  `(def ~fname ~(spice* deps {} fbody)))
