(ns turmeric.new)

(defn- ->sym [word]
  (-> word name symbol))

(defn- map->vec [binds]
  (into [] cat binds))

(defrecord Lambda [deps binds body])

(defn bind [{:keys [deps binds body]} new-binds]
  (let [binds (into binds (map #(update % 0 ->sym)) new-binds)]
    (->Lambda deps binds body)))

(defn beta-reduce [{:keys [binds body]}]
  (eval `(let ~(map->vec binds) ~body)))

(defmacro lambda [deps body]
  `(identity ~(->Lambda deps {} body)))

(defmacro defer [name deps body]
  `(def ~name ~(->Lambda deps {} bodys)))
