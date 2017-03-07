(ns turmeric.core-test
  (:require [clojure.test :refer :all]
            [turmeric.core]))

(deftest keys-to-syms_returns-symbols
  (testing "keys-to-syms returns a vector of symbols"
    (is (every? symbol? (#'turmeric.core/keys-to-syms {'a 1 'b 2 'c 3}))
      "when all keys are symbols")
    (is (every? symbol? (#'turmeric.core/keys-to-syms {"a" 1 "b" 2 "c" 3}))
      "when all keys are strings")
    (is (every? symbol? (#'turmeric.core/keys-to-syms {:a 1 :b 2 :c 3}))
      "when all keys are keywords")
    (is (every? symbol? (#'turmeric.core/keys-to-syms {'a 1 "b" 2 :c 3}))
      "when keys are a mix of symbols, strings and keywords")))

(deftest keys-to-syms_returns-empty
  (testing "keys-to-syms return an empty vector"
    (is (empty? (#'turmeric.core/keys-to-syms {}))
      "when the map provided is empty")))

(deftest binds->vector_returns-vector
  (testing "binds->vector returns a vector"
    (let [data {:a 1 :b 2 :c 3}]
      (is (every? symbol? (take-nth 2 (#'turmeric.core/binds->vector data)))
        "where every odd element is a symbol")
      (is (even? (count (#'turmeric.core/binds->vector data)))
        "with an even number of elements"))))

(deftest binds->vector_return-empty
  (testing "binds->vector return an empty vector"
    (is (empty? (#'turmeric.core/binds->vector {}))
      "when the map provided is empty")))

(deftest spice_defer-form
  (testing "spice will defer a form until all parameters are fulfilled"
    (let [deferred-form (#'turmeric.core/spice* '[a b] {} '(+ a b))]
      (is (fn? deferred-form)
        "spice returns a function when passed two parameters, a and b")

      (testing "and may receive it's parameters in order"
        (let [deferred-form (deferred-form {:a 4})]
          (is (fn? deferred-form)
            "the deferred form returns a function after receiving a")
          (is (= 7 (deferred-form {:b 3}))
            "the deferred form evaluates it's form after receiving b")))

      (testing "and may receive it's parameters out of order"
        (let [deferred-form (deferred-form {:b 2})]
          (is (fn? deferred-form)
            "the deferred form returns a function after receiving b")
          (is (= 9 (deferred-form {:a 7}))
            "the deferred form evaluates it's form after receiving a")))

      (testing "and may receive it's parameters all together"
        (is (= 4 (deferred-form {:a 3 :b 1}))
          "the deferred form evaluates it's form after receiving both a and b")))))

(deftest spice_defer-function
  (testing "spice will defer another function until all parameters are fulfilled"
    (let [deferred-func (#'turmeric.core/spice* '[a b] {} (fn [c] '(+ a b c)))]
      (is (fn? deferred-func)
        "spice returns a function when passed two parameters, a and b")

     (testing "and may receive it's parameters in order"
       (let [deferred-func (deferred-func {:a 2})]
         (is (fn? deferred-func)
           "the deferred function returns a function after receiving a")
         (let [deferred-func (deferred-func {:b 4})]
           (is (fn? deferred-func)
             "the deferred function returns a function after receiving b")
           (is (= 9 (deferred-func 3)))
           "the function evaluates after receiving c")))

     (testing "and may receive it's parameters out of order"
       (let [deferred-func (deferred-func {:b 7})]
         (is (fn? deferred-func)
           "the deferred function returns a function after receiving b")
         (let [deferred-func (deferred-func {:a 1})]
           (is (fn? deferred-func)
             "the deferred function returns a function after receiving a")
           (is (= 11 (deferred-func 3))
             "the function evaluates after receiving c"))))

     (testing "and may receive it's paramters all at once"
       (let [deferred-func (deferred-func {:a 5 :b 5})]
         (is (= 13 (deferred-func 3))
           "the function evaluates after receiving c")))

     (testing "and may receive a parameter also bound in the interior function"
       (let [deferred-func (deferred-func {:a 6 :b 3 :c 8})]
         (is (fn? deferred-func)
           "the interior function is not evaluated")
         (is (= 12 (deferred-func 3))
           "the value of c passed to the interior is evaluated correctly"))))))

(deftest spice_return-immediately
  (let [deferred-now (#'turmeric.core/spice* [] {} (str "that was" " quick"))]
    (is (= "that was quick" deferred-now)
      "the deferred function returns immediately when not given parameters")))

;; TODO: exception testing
