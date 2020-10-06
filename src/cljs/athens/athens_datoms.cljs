(ns athens.athens-datoms)

;; athens namespaced pages that are updated when app is updated
(def datoms
  [{:node/title     "athens/Welcome",
    :block/children [{:block/string "Welcome to Athens, Open-Source Networked Thought!",
                      :block/uid    "a6f7b01cf",
                      :block/open   true,
                      :db/id        339,
                      :block/order  0}
                     {:block/string   "Markup Features",
                      :block/children [{:block/string "Bold text with **double asterisks**",
                                        :block/uid    "c9e48f596",
                                        :block/open   true,
                                        :db/id        341,
                                        :block/order  0}
                                       {:block/string "Mono-spaced text with `backticks`",
                                        :block/uid    "9f727fd2b",
                                        :block/open   true,
                                        :db/id        342,
                                        :block/order  1}
                                       {:block/string "Links with `[[]]`: [[athens/Welcome]]",
                                        :block/uid    "5d19451db",
                                        :block/open   true,
                                        :db/id        343,
                                        :block/order  2}
                                       {:block/string "Links with `#` or `#[[]]` : #athens/Welcome",
                                        :block/uid    "d28dc8467",
                                        :block/open   true,
                                        :db/id        345,
                                        :block/order  3}
                                       {:block/string   "Block references with `(())`: ((82247e489))",
                                        :block/children [{:block/string "I am being referenced",
                                                          :block/_refs  [#:db{:id 347}],
                                                          :block/uid    "82247e489",
                                                          :block/open   true,
                                                          :db/id        362,
                                                          :block/order  0}],
                                        :block/uid      "ddcf4ba1f",
                                        :block/open     true,
                                        :db/id          347,
                                        :block/order    4}
                                       {:block/string "{{[[TODO]]}} `ctrl-enter` to cycle between TODO and DONE",
                                        :block/uid    "5ac7f905f",
                                        :block/open   true,
                                        :db/id        348,
                                        :block/order  5}
                                       {:block/string   "embeds with `{{[[youtube: ]]}}` and `{{``iframe: }}`",
                                        :block/children [{:block/string "{{[[youtube]]: https://www.youtube.com/watch?v=dQw4w9WgXcQ}}",
                                                          :block/uid    "2da5522a1",
                                                          :block/open   true,
                                                          :db/id        352,
                                                          :block/order  0}
                                                         {:block/string "{{iframe: https://www.openstreetmap.org/export/embed.html?bbox=-0.004017949104309083%2C51.47612752641776%2C0.00030577182769775396%2C51.478569861898606&layer=mapnik}}",
                                                          :block/uid    "50cfadc73",
                                                          :block/open   true,
                                                          :db/id        353,
                                                          :block/order  1}],
                                        :block/uid      "f22247778",
                                        :block/open     false,
                                        :db/id          350,
                                        :block/order    6}
                                       {:block/string "images with `![]()` ![athens-splash](https://raw.githubusercontent.com/athensresearch/athens/master/doc/athens-puk-patrick-unsplash.jpg)",
                                        :block/uid    "2af204111",
                                        :block/open   true,
                                        :db/id        351,
                                        :block/order  7}],
                      :block/uid      "f5dd95e6e",
                      :block/open     false,
                      :db/id          340,
                      :block/order    1}
                     {:block/string   "Shortcuts",
                      :block/children [{:block/string "`ctrl-b`: **bold**",
                                        :block/uid    "19c858229",
                                        :block/open   true,
                                        :db/id        355,
                                        :block/order  0}
                                       {:block/string "`ctrl-\\`: open left sidebar",
                                        :block/uid    "33f88d8d6",
                                        :block/open   true,
                                        :db/id        364,
                                        :block/order  1}
                                       {:block/string "`ctrl-shift-\\`: open right sidebar",
                                        :block/uid    "72d86bbb0",
                                        :block/open   true,
                                        :db/id        365,
                                        :block/order  2}
                                       {:block/string "`ctrl-k`: open search bar",
                                        :block/uid    "c993bf326",
                                        :block/open   true,
                                        :db/id        366,
                                        :block/order  3}],
                      :block/uid      "eda8f737a",
                      :block/open     false,
                      :db/id          354,
                      :block/order    2}
                     {:block/string   "Bullets",
                      :block/children [{:block/string "Indent or unindent bullets with tab and shift-tab.",
                                        :block/uid    "d6c47a7f4",
                                        :block/open   true,
                                        :db/id        373,
                                        :block/order  0}
                                       {:block/string "Drag and drop them.",
                                        :block/uid    "2f53541d7",
                                        :block/open   true,
                                        :db/id        375,
                                        :block/order  1}
                                       {:block/string "Select multiple bullets with click and drag or shift-up or shift-down.",
                                        :block/uid    "41a752cb5",
                                        :block/open   true,
                                        :db/id        376,
                                        :block/order  2}],
                      :block/uid      "a0b16ab19",
                      :block/open     false,
                      :db/id          372,
                      :block/order    3}
                     {:block/string   "Left Sidebar",
                      :block/children [{:block/string "Mark a page as a shortcut with the caret next to the page title.",
                                        :block/uid    "a82850462",
                                        :block/open   true,
                                        :db/id        371,
                                        :block/order  0}],
                      :block/uid      "020a90740",
                      :block/open     false,
                      :db/id          368,
                      :block/order    4}
                     {:block/string   "Right Sidebar",
                      :block/children [{:block/string "Open a block or page in the right sidebar by shift clicking on the title or bullet.",
                                        :block/uid    "4e12e40ed",
                                        :block/open   true,
                                        :db/id        370,
                                        :block/order  0}],
                      :block/uid      "539723d85",
                      :block/open     false,
                      :db/id          369,
                      :block/order    5}
                     {:block/string "[[athens/Welcome]] and [[athens/Changelog]] are reserved pages. When a new version of Athens is deployed, your app will update automatically. These pages will be updated as well. Any changes you make to these pages will be overwritten, so don't write anything you need in these pages!",
                      :block/uid    "0250cd89f",
                      :block/open   true,
                      :db/id        377,
                      :block/order  6}
                     {:db/id 689,
                      :block/uid "3938f6d7b",
                      :block/string "Athens is persisted to your filesystem at `documents/athens`. Soon you will be able to choose any location for your db (including Dropbox folders).",
                      :block/open true,
                      :block/order 7}],
    :block/uid      "0",
    :db/id          1,
    :page/sidebar   999}
   {:node/title     "athens/Changelog",
    :block/children [{:block/string   "[[September 29, 2020]]",
                      :block/children [{:block/string "The beginning of the in-Athens Changelog.",
                                        :block/uid    "8eb0523bd",
                                        :block/open   true,
                                        :db/id        382,
                                        :block/order  0}],
                      :block/uid      "52604194d",
                      :block/open     true,
                      :db/id          380,
                      :block/order    0}],
    :block/uid      "1",
    :db/id          378
    :page/sidebar   1000}])