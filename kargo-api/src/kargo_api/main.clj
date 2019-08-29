(ns kargo-api.main
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.test :as test]
            [clojure.pprint :as pprint]
            [kargo-api.logic :as logic]
            [clj-time.core :as t]
            [cheshire.core :as che]))

(defn response
  [status body & {:as headers}]
  {:status status
   :body body
   :headers headers})

(def ok (partial response 200))

(def home-page
  {:name :home-page
   :enter
   (fn [ctx]
     (let [request (:request ctx)
           response (ok "Welcome Homes")]
       (assoc ctx :response response)))})

(def ctx-page
  {:name :ctx-page
   :enter
   (fn [ctx]
     (let [request (:request ctx)
           response (ok (:path-params request))]
       (assoc ctx :response response)))})

(def db
  {:shipper
   {1 {:id 1 
       :name "ali"}}
   :jobs
   {1 {:id 1
       :origin "jakarta"
       :destination "bandung"
       :budget 100
       :shipment-date ""
       :shipper-id 1}
    2 {:id 2
       :origin "aceh"
       :destination "papua"
       :budget 200
       :shipment-date ""
       :shipper-id 1}
    3 {:id 3
       :origin "jakarta"
       :destination "medan"
       :budget 300
       :shipment-date ""
       :shipper-id 1}
    4 {:id 4
       :origin "bogor"
       :destination "papua"
       :budget 250
       :shipment-date ""
       :shipper-id 1}}
   :bids
   {1 {:id 1
       :price 50
       :vehicle "truk"
       :jobs-id 1
       :transporter-id 1}
    2 {:id 2
       :price 150
       :vehicle "bus"
       :jobs-id 1
       :transporter-id 1}
    3 {:id 3
       :price 250
       :vehicle "pesawat"
       :jobs-id 1
       :transporter-id 1}
    4 {:id 4
       :price 50
       :vehicle "pesawat"
       :jobs-id 1
       :transporter-id 1}}
   :transporter
   {1 {:id 1
       :name "kargo"}}})

(def db-page
  {:name :ctx-page
   :enter
   (fn [ctx]
     (let [request (:request ctx)
           response (ok (:path-params request))]
       (assoc ctx :response response)))})

(def a (atom {}))

(def bid-page
  {:name :bid-page
   :enter
   (fn [ctx]
     (let [request (:request ctx)
           params (:path-params request)
           job-id (read-string (:job-id params))
           key (keyword (:key params))
           bids (:bids db)
           bid-by-job-id (filter #(= job-id (-> % val :jobs-id)) bids)
           bid-by-job-id (mapv val bid-by-job-id)
           sorted-bid-by-key (logic/bubble-sort-key bid-by-job-id key)
           json-sorted-bid-by-key (che/generate-string sorted-bid-by-key)
           ;;_ (reset! a [job-by-id ])
           response (ok json-sorted-bid-by-key)]
       (assoc ctx :response response)))})


(def job-page
  {:name :job-page
   :enter
   (fn [ctx]
     (let [request (:request ctx)
           response (ok (:path-params request))
           params (:path-params request)
           key (keyword (:key params))
           jobs (:jobs db)
           jobs (mapv val jobs)
           sorted-job-by-key (logic/bubble-sort-key jobs key)
           json-sorted-job-by-key (che/generate-string sorted-job-by-key)
           ;;_ (reset! a sorted-job-by-key)
           response (ok json-sorted-job-by-key)]
       (assoc ctx :response response)))})

(def routes
  (route/expand-routes
   #{["/" :get home-page :route-name :home-page]
     ["/ctx" :get ctx-page :route-name :ctx-page]
     ["/db" :get db-page :route-name :db-page]
     ["/bid/:job-id/sort-by/:key" :get bid-page :route-name :bid-page]
     ["/job/sort-by/:key" :get job-page :route-name :job-page]}))

(def service-map
  {::http/routes routes
   ::http/type :jetty
   ::http/port 8080})

(defonce server (atom nil))

(defn start-dev
  []
  (reset! server
          (http/start (http/create-server (assoc service-map ::http/join? false)))))

(defn stop-dev
  []
  (http/stop @server))

(defn restart-dev
  []
  (stop-dev)
  (start-dev))

(def service-test
  (::http/service-fn (http/create-servlet service-map)))

#_(test/response-for service-test :get "/bid/1/sort-by/key")
#_(test/response-for service-test :get "/job/sort-by/key")
