(ns turmeric.core)

(defn- ->sym [word]
  (-> word name symbol))

(defn- map->vec [binds]
  (into [] cat binds))

(defn- vec->map [binds]
  (into {} (map vec) (partition 2 binds)))

(defrecord DeferredExpression [needs bound body])

(defmacro spice [needs body]
  `(->DeferredExpression '~needs {} '~body))

(defmacro defspice [name needs body]
  `(def ~name (->DeferredExpression '~needs {} '~body)))

(defn add
  ([defer key val & binds]
   (add defer (vec->map (into [key val] binds))))
  ([{:keys [bound] :as defer} binds]
   (let [syms<-keys (map #(update % 0 ->sym))]
     (update defer :bound #(into % syms<-keys binds)))))

(defn mix
  ([defer binds]
   (mix (add defer binds)))
  ([defer key val & binds]
   (mix (apply add defer key val binds)))
  ([{:keys [needs bound body] :as defer}]
   (if (= needs (keys bound))
     (eval `(let ~(map->vec bound) ~body))
     (let [unbound-needs (filter (-> bound keys set complement) needs)
           new-body     `(let ~(map->vec bound) ~body)]
       (eval `(spice ~(vec unbound-needs) ~new-body))))))
