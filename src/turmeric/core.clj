(ns turmeric.core
  (:require [clojure.set :refer [subset?]]))

(defn- keys-to-syms [m]
  (let [to-sym #(-> % name symbol)]
    (map to-sym (keys m))))

(defn- binds->vector [bindings]
  (reduce-kv #(conj %1 (-> %2 name symbol) %3) [] bindings))

(defn- spice* [deps binds fbody]
  (if (subset? (set deps) (-> binds keys-to-syms set))
    `(let ~(binds->vector binds) ~fbody)
    `(fn [bmap#]
      (let [deps#  (quote ~deps)
            binds# (merge bmap# ~binds)
            fbody# (quote ~fbody)]
        (eval ((var spice*) deps# binds# fbody#))))))

(defn spice [deps fbody]
  (eval (spice* deps {} fbody)))

(defmacro defer [fname deps fbody]
  `(def ~fname ~(spice* deps {} fbody)))