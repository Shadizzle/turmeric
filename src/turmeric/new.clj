(ns turmeric.new)

(defn- to-sym [word]
  (println word)
  (-> word name symbol))

(defn- map->vec [binds]
  (into [] cat binds))

(defrecord Lambda [deps binds body])

(defn bind [{:keys [deps binds body]} new-binds]
  (let [binds (into binds #((do (println %) (update % 0 to-sym))) new-binds)]
    (->Lambda deps binds body)))

(defn beta-reduction [{:keys [binds body]}]
  (eval `(let ~(map->vec binds) ~body)))

(defmacro spice [deps body]
  (->Lambda deps {} body))
