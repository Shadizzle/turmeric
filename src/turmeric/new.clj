(ns turmeric.new)

(defn- ->sym [word]
  (-> word name symbol))

(defn- map->vec [binds]
  (into [] cat binds))

(defrecord Spice [deps binds body])

(defn add [{:keys [binds] :as spice} new-binds]
  (let [sym<-key (map #(update % 0 ->sym))]
    (update spice :binds #(into % sym<-key new-binds))))

(defn mix [{:keys [deps binds body]}]
  (if (= deps (keys binds))
    (eval `(let ~(map->vec binds) ~body))
    (let [unbound-deps (vec (filter (complement (set (keys binds))) deps))
          new-body     `(let ~(map->vec binds) ~body)] ;; TODO Use sym swapping instead
      (eval `(identity ~(->Spice unbound-deps {} new-body))))))

(defmacro spice [deps body]
  `(identity ~(->Spice deps {} body)))

(defmacro defer [name deps body]
  `(def ~name ~(->Spice deps {} body)))
