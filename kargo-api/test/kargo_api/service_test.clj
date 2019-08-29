(ns kargo-api.service-test
  (:require [clojure.test :refer :all]
            [io.pedestal.test :refer :all]
            [io.pedestal.http :as bootstrap]
            [kargo-api.service :as service]
            [kargo-api.main :as main]
            [kargo-api.logic :as logic]
            [cheshire.core :as che]))

 (def service
   (::bootstrap/service-fn (bootstrap/create-servlet main/service-map)))

;; (deftest home-page-test
;;   (is (=
;;        (:body (response-for service :get "/"))
;;        "Hello World!"))
;;   (is (=
;;        (:headers (response-for service :get "/"))
;;        {"Content-Type" "text/html;charset=UTF-8"
;;         "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
;;         "X-Frame-Options" "DENY"
;;         "X-Content-Type-Options" "nosniff"
;;         "X-XSS-Protection" "1; mode=block"
;;         "X-Download-Options" "noopen"
;;         "X-Permitted-Cross-Domain-Policies" "none"
;;         "Content-Security-Policy" "object-src 'none'; script-src 'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:;"})))

;; (deftest about-page-test
;;   (is (.contains
;;        (:body (response-for service :get "/about"))
;;        "Clojure 1.9"))
;;   (is (=
;;        (:headers (response-for service :get "/about"))
;;        {"Content-Type" "text/html;charset=UTF-8"
;;         "Strict-Transport-Security" "max-age=31536000; includeSubdomains"
;;         "X-Frame-Options" "DENY"
;;         "X-Content-Type-Options" "nosniff"
;;         "X-XSS-Protection" "1; mode=block"
;;         "X-Download-Options" "noopen"
;;         "X-Permitted-Cross-Domain-Policies" "none"
;;         "Content-Security-Policy" "object-src 'none'; script-src 'unsafe-inline' 'unsafe-eval' 'strict-dynamic' https: http:;"})))

(def sorted-bid-by-price
  [{:id 1, :price 50, :vehicle "truk", :jobs-id 1, :transporter-id 1}
   {:id 4,
    :price 50,
    :vehicle "pesawat",
    :jobs-id 1,
    :transporter-id 1}
   {:id 2, :price 150, :vehicle "bus", :jobs-id 1, :transporter-id 1}
   {:id 3,
    :price 250,
    :vehicle "pesawat",
    :jobs-id 1,
    :transporter-id 1}])

(def sorted-job-by-budget
  [{:id 1,
  :origin "jakarta",
  :destination "bandung",
  :budget 100,
  :shipment-date "",
  :shipper-id 1}
 {:id 2,
  :origin "aceh",
  :destination "papua",
  :budget 200,
  :shipment-date "",
  :shipper-id 1}
 {:id 4,
  :origin "bogor",
  :destination "papua",
  :budget 250,
  :shipment-date "",
  :shipper-id 1}
 {:id 3,
  :origin "jakarta",
  :destination "medan",
  :budget 300,
  :shipment-date "",
  :shipper-id 1}])

(deftest bubble-sort-test
  (is (= (logic/bubble-sort [1 4 3 2 5]) [1 2 3 4 5])))

(deftest bid-sort-api
  (is (let [response (response-for service :get "/bid/1/sort-by/price")
            status (:status response)
            body (:body response)]
        (and (= status 200) (= body (che/generate-string sorted-bid-by-price))))))

(deftest job-sort-api
  (is (let [response (response-for service :get "/job/sort-by/budget")
            status (:status response)
            body (:body response)]
        (and (= status 200) (= body (che/generate-string sorted-job-by-budget))))))
