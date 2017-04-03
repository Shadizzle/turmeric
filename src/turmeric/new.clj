(ns turmeric.new)

(defn- ->sym [word]
  (-> word name symbol))

(defn- map->vec [binds]
  (into [] cat binds))

(defrecord Spice [deps binds body])

(defn add [{:keys [binds] :as spice} new-binds]
  (let [sym<-key (map #(update % 0 ->sym))]
    (update spice :binds #(into % sym<-key new-binds))))

(defn mix [{:keys [binds body]}]
  (eval `(let ~(map->vec binds) ~body)))

(defmacro spice [deps body]
  `(identity ~(->Spice deps {} body)))

(defmacro defer [name deps body]
  `(def ~name ~(->Spice deps {} body)))
