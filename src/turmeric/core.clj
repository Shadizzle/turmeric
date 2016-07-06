(ns turmeric.core
  (:require [clojure.set :refer [subset?]]))

(defn- to-sym [v]
  (-> v name symbol))

(defn- keys-to-syms [m]
  (map to-sym (keys m)))

(defn- binds->vector [bindings]
  (reduce-kv #(conj %1 (to-sym %2) %3) [] bindings))

(defn- spice* [deps binds fbody]
  (if (subset? (set deps) (-> binds keys-to-syms set))
    `(let ~(binds->vector binds) ~fbody)
    `(fn [bmap#]
      (let [deps#  (quote ~deps)
            binds# (merge bmap# ~binds)
            fbody# (quote ~fbody)]
        (eval ((var spice*) deps# binds# fbody#))))))

(defmacro spice [deps fbody]
  `(identity ~(spice* deps {} fbody)))

(defmacro defer [fname deps fbody]
  `(def ~fname ~(spice* deps {} fbody)))
