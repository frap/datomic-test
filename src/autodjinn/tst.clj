(ns autodjinn.tst)

(require '[datomic.api :as d])
(def uri "datomic:mem://test")
(d/create-database uri)
(def conn (d/connect uri))

(d/transact conn [ ;; User
                 {:db/id #db/id [:db.part/db]
                  :db/ident :user/username
                  :db/valueType :db.type/string
                  :db/cardinality :db.cardinality/one
                  :db/unique :db.unique/value
                  :db/index true
                  :db/doc "This User's username"
                  :db.install/_attribute :db.part/db}
                 ;; Trust
                 {:db/id #db/id [:db.part/db]
                  :db/ident :trust/level
                  :db/valueType :db.type/long
                  :db/cardinality :db.cardinality/one
                  :db/doc "The trust of this data"
                  :db.install/_attribute :db.part/db}
                 ;; Device
                 {:db/id #db/id [:db.part/db]
                  :db/ident :device/name
                  :db/valueType :db.type/string
                  :db/cardinality :db.cardinality/one
                  :db/fulltext true
                  :db/doc "This device's name"
                  :db.install/_attribute :db.part/db}
                 {:db/id #db/id [:db.part/db]
                  :db/ident :device/intface
                  :db/valueType :db.type/ref
                  :db/cardinality :db.cardinality/many
                  :db/isComponent true
                  :db/doc "An interface config of the device"
                  :db.install/_attribute :db.part/db}
                 {:db/id #db/id [:db.part/db]
                  :db/ident :device/user
                  :db/valueType :db.type/ref
                  :db/cardinality :db.cardinality/many
                  :db/doc "This devices's users"
                  :db.install/_attribute :db.part/db}
                 ;; Interfaces
                 {:db/id #db/id [:db.part/db]
                  :db/ident :intface/name
                  :db/valueType :db.type/string
                  :db/cardinality :db.cardinality/one
                  :db/fulltext true
                  :db/doc "This interface's name"
                  :db.install/_attribute :db.part/db}
                 {:db/id #db/id [:db.part/db]
                  :db/ident :intface/L2
                  :db/valueType :db.type/ref
                  :db/cardinality :db.cardinality/one
                  :db/isComponent true
                  :db/doc "This interface's L2 config"
                  :db.install/_attribute :db.part/db}
                 {:db/id #db/id [:db.part/db]
                  :db/ident :intface/L3
                  :db/valueType :db.type/ref
                  :db/cardinality :db.cardinality/one
                  :db/isComponent true
                  :db/doc "This interface's L3 config"
                  :db.install/_attribute :db.part/db}
                 ;; L2 config
                 {:db/id #db/id [:db.part/db]
                  :db/ident :L2/tag
                  :db/valueType :db.type/long
                  :db/cardinality :db.cardinality/one
                  :db/fulltext true
                  :db/doc "The L2 VLAN number"
                  :db.install/_attribute :db.part/db}
                 {:db/id #db/id [:db.part/db]
                  :db/ident :L2/address
                  :db/valueType :db.type/string
                  :db/cardinality :db.cardinality/one
                  :db/fulltext true
                  :db/doc "This L2 interface's MAC address"
                  :db.install/_attribute :db.part/db}
                  {:db/id #db/id [:db.part/db]
                  :db/ident :L2/infotrust
                  :db/valueType :db.type/ref
                  :db/cardinality :db.cardinality/one
                  :db/doc "This trust in Layer2 collected information"
                  :db.install/_attribute :db.part/db}
                 ;; L3 config
                 {:db/id #db/id [:db.part/db]
                  :db/ident :L3/VRF
                  :db/valueType :db.type/string
                  :db/cardinality :db.cardinality/one
                  :db/fulltext true
                  :db/doc "The L3 VRF"
                  :db.install/_attribute :db.part/db}
                 {:db/id #db/id [:db.part/db]
                  :db/ident :L3/address
                  :db/valueType :db.type/string
                  :db/cardinality :db.cardinality/one
                  :db/fulltext true
                  :db/doc "This L3 interface's IP address"
                  :db.install/_attribute :db.part/db}
                 {:db/id #db/id [:db.part/db]
                  :db/ident :L3/infotrust
                  :db/valueType :db.type/ref
                  :db/cardinality :db.cardinality/one
                  :db/doc "This trust in Layer3 collected information"
                  :db.install/_attribute :db.part/db}
                 ])

(d/transact conn
            [;; A owner of a device
             {:db/id #db/id [:db.part/user -100]
              :user/username "andrew.gasson"}
             ;; some trust values
             {:db/id #db/id [:db.part/user -200]
              :trust/level 9000 }
             {:db/id #db/id [:db.part/user -210]
              :trust/level 1000 }
             ;; L2 info
             {:db/id #db/id [:db.part/user -600]
              :L2/tag 22
              :L2/address "aadd:0011:ccdd"
              :L2/infotrust #db/id [:db.part/user -200]}
             {:db/id #db/id [:db.part/user -610]
              :L2/tag 999
              :L2/address "d345:c098:0123"
              :L2/infotrust #db/id [:db.part/user -200]}
             ;; L3 info
             {:db/id #db/id [:db.part/user -700]
              :L3/VRF     "red"
              :L3/address "10.1.1.1"
              :L3/infotrust #db/id [:db.part/user -210]}
             {:db/id #db/id [:db.part/user -710]
              :L3/address "66.66.66.66"
              :L3/infotrust #db/id [:db.part/user -210]}
             ;; Interfaces
             {:db/id #db/id [:db.part/user -500]
              :intface/name "Gigabit 1/0"
              :intface/L2 #db/id [:db.part/user -600]
              :intface/L3 #db/id [:db.part/user -700]}
             {:db/id #db/id [:db.part/user -505]
              :intface/name "ATM 1/1/1"
              :intface/L2 #db/id [:db.part/user -610]
              :intface/L3 #db/id [:db.part/user -710]}
             ;; Device

             {:db/id #db/id [:db.part/user -300]
              :device/name "Gas PC"
              :device/user #db/id [:db.part/user -100]
              :device/intface #db/id [:db.part/user -505]
              ;;:device/intface #db/id [:db.part/user -500]
              }
             ;; Another user
             {:db/id #db/id [:db.part/user -101]
              :user/username "womble.norm@bizurk.com"}

             ;; Another user writing an article
             {:db/id #db/id [:db.part/user -102]
              :user/username "alex.hill"}
             ;; another trust level
             {:db/id #db/id [:db.part/user -201]
              :trust/level 500}
             ])

;; Find trust entities
(d/q '[:find ?tid ?t
       :in $ ?u
       :where
       [?uid :user/username ?u]
       [?aid :device/intface ?iid]
       [?iid :intface/L2 ?2id]
       [?tid :trust/level ?t]
       ]
     (d/db conn) "andrew.gasson")

;; Find config dont trust
(d/q '[:find ?2id ?t ?i
       :in $ ?u
       :where
       [?uid :user/username ?u]
       [?did :device/user ?uid]
       [?did :device/intface ?i]
       [?i   :intface/L2 ?2id]
       [?tid :trust/level ?t]
       ]
     (d/db conn) "andrew.gasson")
