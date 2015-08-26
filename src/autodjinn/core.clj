(ns autodjinn.core
  (:require  [clojure.java.io :as io]
             [clojure-mail.core :refer :all]
             [clojure-mail.message :as message]
             [nomad :refer [defconfig]]
             [datomic.api :as d]
             ;[autodjinn.schema :refer :all]
             ))
;; in autodjinn/core.clj:
(defconfig config (io/resource "config/autodjinn-config.edn"))
(def db-uri (get (config) :db-uri))

(println db-uri)
(d/create-database db-uri)
(def db-conn (d/connect db-uri))
(defn new-db-val [] (d/db db-conn))

(def devschema
  [
 ;; device attributes
 {:db/id #db/id[:db.part/db]
  :db/ident :device/uuid
  :db/valueType :db.type/uuid
  :db/cardinality :db.cardinality/one
  :db/unique :db.unique/identity
  :db/index true
  :db/doc "The globally unique device's UUID"
  :db.install/_attribute :db.part/db}

 {:db/id #db/id[:db.part/db]
  :db/ident :device/name
  :db/valueType :db.type/string
  :db/cardinality :db.cardinality/many
  :db/fulltext true
  :db/index true
  :db/doc "The device's name"
  :db.install/_attribute :db.part/db}

 {:db/id #db/id[:db.part/db]
  :db/ident :device/version
  :db/valueType :db.type/string
  :db/cardinality :db.cardinality/one
  :db/index true
  :db/doc "The device's software version"
  :db.install/_attribute :db.part/db}

 {:db/id #db/id[:db.part/db]
  :db/ident :device/type
  :db/valueType :db.type/ref
  :db/cardinality :db.cardinality/one
  :db/doc "Enum, one of :device.type/router, :device.type/switch, :device.type/endpoint."
  :db.install/_attribute :db.part/db}

 {:db/id #db/id[:db.part/db]
  :db/ident :device/iface
  :db/valueType :db.type/ref
  :db/cardinality :db.cardinality/many
  :db/doc "The device's interfaces"
  :db.install/_attribute :db.part/db}

{:db/id #db/id[:db.part/db]
  :db/ident :device/owner
  :db/valueType :db.type/ref
  :db/cardinality :db.cardinality/many
  :db/doc "The device's owner"
  :db.install/_attribute :db.part/db}

 {:db/id #db/id[:db.part/db]
  :db/ident :iface/name
  :db/valueType :db.type/string
  :db/cardinality :db.cardinality/one
  :db/index true
  :db/doc "The interface name"
  :db.install/_attribute :db.part/db}

 {:db/id #db/id[:db.part/db]
  :db/ident :iface/L2
  :db/valueType :db.type/ref
  :db/cardinality :db.cardinality/one
  :db/doc "The interface's L2 config"
  :db.install/_attribute :db.part/db}

;; Layer 2 attributes
{:db/id #db/id[:db.part/db]
  :db/ident :L2/address
  :db/valueType :db.type/string
  :db/cardinality :db.cardinality/one
  :db/fulltext true
  :db/index true
  :db/doc "The interface MAC address"
  :db.install/_attribute :db.part/db}

{:db/id #db/id[:db.part/db]
  :db/ident :L2/tag
  :db/valueType :db.type/long
  :db/cardinality :db.cardinality/many
  :db/index true
  :db/doc "The layer2 VLAN(s)"
  :db.install/_attribute :db.part/db}

{:db/id #db/id[:db.part/db]
  :db/ident :L2/service
  :db/valueType :db.type/ref
  :db/cardinality :db.cardinality/many
  :db/fulltext true
  :db/index true
  :db/doc "Layer2's service config"
  :db.install/_attribute :db.part/db}

;; layer 3 attributes
{:db/id #db/id[:db.part/db]
  :db/ident :L2/L3
  :db/valueType :db.type/ref
  :db/cardinality :db.cardinality/many
  :db/doc "The Layer2's layer3 config"
  :db.install/_attribute :db.part/db}

{:db/id #db/id[:db.part/db]
  :db/ident :L3/address
  :db/valueType :db.type/string
  :db/cardinality :db.cardinality/many
  :db/fulltext true
  :db/index true
  :db/doc "The interface IP address(es)"
  :db.install/_attribute :db.part/db}

{:db/id #db/id[:db.part/db]
  :db/ident :L3/service
  :db/valueType :db.type/ref
  :db/cardinality :db.cardinality/many
  :db/fulltext true
  :db/index true
  :db/doc "Layer3's service config"
  :db.install/_attribute :db.part/db}

{:db/id #db/id[:db.part/db]
  :db/ident :service/name
  :db/valueType :db.type/string
  :db/cardinality :db.cardinality/one
  :db/fulltext true
  :db/index true
  :db/doc "service name"
  :db.install/_attribute :db.part/db}

{:db/id #db/id[:db.part/db]
  :db/ident :service/port
  :db/valueType :db.type/long
  :db/cardinality :db.cardinality/one
  :db/fulltext true
  :db/index true
  :db/doc "service port"
  :db.install/_attribute :db.part/db}

])

(defn update-schema []
  (d/transact db-conn devschema))

(def test-data {:devname "gases computer"
                :l3-addr "10.1.1.1"
                :l2-vlan 123
                :email   "agasson@red-elvis.net"
                :iface "Gigabit 1/0"
                :version "Cisco IOS 12.4(T)"})

(:devname test-data)

@(d/transact db-conn [{:db/id (d/tempid "db.part/user")
                       :device/name (:devname test-data)
                       :device/uuid (d/squuid)
                  ;;     :L2/tag      (:l2-vlan test-data)
                       :L3/address  (:l3-addr test-data)
                       :iface/name  (:iface   test-data)
                       :device/version  (:version   test-data)
                       }])

(def my-data (d/q '[:find ?eid :where [?eid :iface/name _]] (new-db-val)))

(defn parse-args [args]
  (into {} (map (fn [[k v]] [(keyword (.replace k "--" "")) v])
                (partition 2 args))))

(defn update-schema []
  (d/transact db-conn schema1-txn))

(defn create-mail [attrs]
  (d/transact db-conn [(merge {:db/id (d/tempid "db.part/user")}
                              attrs)]))
