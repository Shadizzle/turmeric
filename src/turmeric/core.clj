(ns turmeric.core
  (:require [clojure.set :refer [subset? intersection difference]]
            [turmeric.exceptions :as e]))

(defn- to-sym [v]
  (-> v name symbol))

(defn- keys->syms [m]
  (map to-sym (keys m)))

(defn- binds->vector [bindings]
  (reduce-kv #(conj %1 (to-sym %2) %3) [] bindings))

(defn spice* [deps binds fbody]
  (let [deps-set  (set deps)
        binds-set (-> binds keys->syms set)]
    (let [undeclared (difference binds-set deps-set)]
      (if (seq undeclared)
        (throw (e/undeclared-binding undeclared deps binds))))
    (if (= deps-set binds-set)
      (eval `(let ~(binds->vector binds) ~fbody))
      (fn [new-binds]
        (let [new-binds-set (-> new-binds keys->syms set)
              bound-twice   (intersection new-binds-set binds-set)]
          (if (seq bound-twice)
            (throw (e/bindings-already-passed bound-twice deps binds))))
        (let [binds (merge new-binds binds)]
          (spice* deps binds fbody))))))

(defmacro spice [deps fbody]
  `(identity ~(spice* deps {} fbody)))

(defmacro defer [fname deps fbody]
  `(def ~fname ~(spice* deps {} fbody)))
