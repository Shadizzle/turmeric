(ns turmeric.core
  (:require [clojure.set :refer [union difference]]
            [clojure.spec.alpha :as s]))

(s/def ::needs (s/coll-of symbol? :kind vector?))
(s/def ::bound (s/map-of symbol? any?))
(s/def ::body any?)

(s/def ::deferred-expression (s/keys :req-un [::needs ::bound ::body]))

(s/fdef ->DeferredExpression
  :args (s/cat :needs ::needs :bound ::bound :body ::body)
  :ret ::deferred-expression
  :fn (s/and #(= (-> % :ret :needs) (-> % :args :needs))
             #(= (-> % :ret :bound) (-> % :args :bound))
             #(= (-> % :ret :body)  (-> % :args :body))))

(s/def ::binding-key
  (s/or :keyword keyword?
        :string string?
        :symbol symbol?))

(s/def ::bindings
  (s/alt :map (s/map-of ::binding-key any?)
         :key-val (s/+ (s/cat :key ::binding-key :val any?))))

(s/fdef add
  :args (s/cat :defer ::deferred-expression
               :binds ::bindings)
  :ret ::deferred-expression
  :fn (fn [c] ; c for conformed
        (let [ret-needs (-> c :ret :needs set)
              ret-binds (-> c :ret :binds set)
              arg-needs (-> c :args :defer :needs set)
              arg-bound (-> c :args :defer :bound set)
              arg-map (->> c :args :binds :map keys (map ->sym))
              arg-key-val (->> c :args :binds second (map (comp ->sym second :key)))
              arg-binds (set (or arg-map arg-key-val))]
          (and
            (= ret-needs arg-needs)
            (= ret-binds (union arg-bound arg-binds))))))

(s/fdef mix
  :args (s/cat :defer ::deferred-expression
               :binds (s/? ::bindings))
  :ret any?
  :fn (fn [c] ; c for conformed
        (let [ret-needs (-> c :ret :needs set)
              ret-binds (-> c :ret :binds set)
              arg-needs (-> c :args :defer :needs set)
              arg-map (->> c :args :binds :map keys (map ->sym))
              arg-key-val (->> c :args :binds second (map (comp ->sym second :key)))
              arg-binds (set (or arg-map arg-key-val))]
          (and
            (= ret-needs (difference arg-needs arg-binds))
            (= ret-binds {})))))
