(ns athens.parser.impl-test
  (:require
    [athens.parser.impl :as sut]
    [clojure.test :as t :refer [deftest is are testing]]))


(defmacro parses-to
  [parser & tests]
  `(t/are [in# out#] (= out# (do
                               (println in#)
                               (time (~parser in#))))
     ~@tests))


(t/deftest block-structure

  (t/testing "that headings are parsed"

    (parses-to sut/block-parser->ast

               "# Heading"
               [:block [:heading {:n 1}
                        [:paragraph-text "Heading"]]]

               "# Heading\n\n"
               [:block [:heading {:n 1}
                        [:paragraph-text "Heading"]]]

               "### Heading\n"
               [:block [:heading {:n 3}
                        [:paragraph-text "Heading"]]]))

  (t/testing "that thematic-breaks are parsed"

    (parses-to sut/block-parser->ast

               "***"
               [:block [:thematic-break "***"]]

               "---"
               [:block [:thematic-break "---"]]

               "___"
               [:block [:thematic-break "___"]]))

  (t/testing "that indented-code-blocks are parsed"

    (parses-to sut/block-parser->ast

               "    some code"
               [:block [:indented-code-block
                        [:code-text "some code"]]]

               "    multiline\n    code"
               [:block [:indented-code-block
                        [:code-text "multiline\ncode"]]]

               "    multiline\n    code\n      with indentation"
               [:block [:indented-code-block
                        [:code-text "multiline\ncode\n  with indentation"]]]))

  (t/testing "that fenced-code-blocks are parsed"

    (parses-to sut/block-parser->ast

               "```\nsome code```"
               [:block [:fenced-code-block {:lang ""}
                        [:code-text "some code"]]]

               "```javascript\nvar a = 1;\n```"
               [:block [:fenced-code-block {:lang "javascript"}
                        [:code-text "var a = 1;"]]]

               "```javascript\nvar a = \"with` ticks`\";\nand multiline```"
               [:block [:fenced-code-block {:lang "javascript"}
                        [:code-text "var a = \"with` ticks`\";\nand multiline"]]]))

  (t/testing "that paragraphs are parsed"

    (parses-to sut/block-parser->ast

               "aaa"
               [:block [:paragraph-text "aaa"]]

               "aaa\n\nbbb"
               [:block
                [:paragraph-text "aaa"]
                [:paragraph-text "bbb"]]

               "aaa\nbbb\n\nccc\nddd"
               [:block
                [:paragraph-text "aaa\nbbb"]
                [:paragraph-text "ccc\nddd"]]

               "aaa\n\n\nbbb"
               [:block
                [:paragraph-text "aaa"]
                [:paragraph-text "bbb"]]

               "  aaa\n bbb" ;; leading spaces are skipped
               [:block [:paragraph-text "aaa\nbbb"]]

               "aaa\n    bbb\n        ccc"
               [:block [:paragraph-text "aaa\nbbb\nccc"]]

               "   aaa\nbbb" ;; 3 spaces max
               [:block [:paragraph-text "aaa\nbbb"]]

               "    aaa\nbbb" ;; or code block is triggered
               [:block
                [:indented-code-block [:code-text "aaa"]]
                [:paragraph-text "bbb"]]

               "aaa    \nbbb    " ;; final spaces are stripped
               [:block [:paragraph-text "aaa\nbbb"]]))

  (t/testing "that block-quote is parsed"

    (parses-to sut/block-parser->ast

               "> # Foo
> bar
> baz"
               [:block [:block-quote
                        [:heading {:n 1} [:paragraph-text "Foo"]]
                        [:paragraph-text "bar\nbaz"]]]

               ;; spaces after `>` can be omitted
               "># Foo
>bar
> baz"
               [:block [:block-quote
                        [:heading {:n 1} [:paragraph-text "Foo"]]
                        [:paragraph-text "bar\nbaz"]]]

               ;; The > characters can be indented 1-3 spaces
               "   > # Foo
   > bar
 > baz"
               [:block [:block-quote
                        [:heading {:n 1} [:paragraph-text "Foo"]]
                        [:paragraph-text "bar\nbaz"]]]

               ;; Four spaces gives us a code block:
               "    > # Foo
    > bar
    > baz"
               [:block [:indented-code-block [:code-text "> # Foo\n> bar\n> baz"]]]

               ;; block quote is a container for other blocks
               "> aaa
> 
> bbb"
               [:block [:block-quote
                        [:paragraph-text "aaa"]
                        [:paragraph-text "bbb"]]]

               ;; nested block quotes
               "> > aaa
> > bbb
> > ccc"
               [:block
                [:block-quote
                 [:block-quote
                  [:paragraph-text "aaa\nbbb\nccc"]]]]

               ">> aa\n>> bb"
               [:block
                [:block-quote
                 [:block-quote
                  [:paragraph-text "aa\nbb"]]]]

               ">     code

>    not code"
               [:block
                [:block-quote [:indented-code-block [:code-text "code"]]]
                [:block-quote [:paragraph-text "not code"]]]

               "> ```code\n> more```

>    not code"
               [:block
                [:block-quote
                 [:fenced-code-block {:lang "code"} [:code-text "more"]]]
                [:block-quote [:paragraph-text "not code"]]])))


(t/deftest inline-structure

  (t/testing "backslash escapes"
    (parses-to sut/inline-parser->ast

               ;; Any ASCII punctuation character may be backslash-escaped
               "\\!\\\"\\#\\$\\%\\&\\'\\(\\)\\*\\+\\,\\-\\.\\/\\:\\;\\<\\=\\>\\?\\@\\[\\\\\\]\\^\\_\\`\\{\\|\\}\\~"
               ["\\!"
                "\\\""
                "\\#"
                "\\$"
                "\\%"
                "\\&"
                "\\'"
                "\\("
                "\\)"
                "\\*"
                "\\+"
                "\\,"
                "\\-"
                "\\."
                "\\/"
                "\\:"
                "\\;"
                "\\<"
                "\\="
                "\\>"
                "\\?"
                "\\@"
                "\\["
                "\\\\"
                "\\]"
                "\\^"
                "\\_"
                "\\`"
                "\\{"
                "\\|"
                "\\}"
                "\\~"]

               ;; Backslashes before other characters are treated as literal backslashes:
               "\\→\\A\\a\\ \\3\\φ\\«"
               [[:text-run "\\→\\A\\a\\ \\3\\φ\\«"]]))

  (t/testing "code spans"
    (parses-to sut/inline-parser->ast

               ;; code spans
               "`abc`"
               [[:code-span "abc"]]

               "` foo ` bar `"
               [[:code-span " foo ` bar "]]))

  (t/testing "all sorts of emphasis"
    (parses-to sut/inline-parser->ast

               ;; emphasis & strong emphasis
               "*emphasis*"
               [[:emphasis [:text-run "emphasis"]]]

               "* not em *"
               ["*" [:text-run " not em "] "*"]

               "**strong**"
               [[:strong-emphasis [:text-run "strong"]]]

               "_also emphasis_"
               [[:emphasis [:text-run "also emphasis"]]]

               "__very strong__"
               [[:strong-emphasis [:text-run "very strong"]]]

               ;; mix and match different emphasis
               "**bold and *italic***"
               [[:strong-emphasis
                 [:text-run "bold and "]
                 [:emphasis
                  [:text-run "italic"]]]]

               ;; next to each other
               "normal *italic* **bold**"
               [[:text-run "normal "]
                [:emphasis [:text-run "italic"]]
                [:text-run " "]
                [:strong-emphasis [:text-run "bold"]]]

               "_so wrong*"
               ["_" [:text-run "so wrong"] "*"]))

  (t/testing "highlights (local Athens extension `^^...^^`)"
    (parses-to sut/inline-parser->ast

               ;; just a highlight
               "^^NEW^^"
               [[:highlight [:text-run "NEW"]]]

               ;; in a middle
               "something ^^completely^^ different"
               [[:text-run "something "]
                [:highlight [:text-run "completely"]]
                [:text-run " different"]]

               ;; with spaces
               "^^a b c^^"
               [[:highlight [:text-run "a b c"]]]

               ;; mixing with emphasis
               "this ^^highlight *has* **emphasis**^^"
               [[:text-run "this "]
                [:highlight
                 [:text-run "highlight "]
                 [:emphasis [:text-run "has"]]
                 [:text-run " "]
                 [:strong-emphasis [:text-run "emphasis"]]]]

               "this ^^highlight **has *nested emphasis***^^"
               [[:text-run "this "]
                [:highlight
                 [:text-run "highlight "]
                 [:strong-emphasis
                  [:text-run "has "]
                  [:emphasis [:text-run "nested emphasis"]]]]]))

  (t/testing "strikethrough (GFM extension)"
    (parses-to sut/inline-parser->ast

               "~~Hi~~ Hello, world!"
               [[:strikethrough [:text-run "Hi"]]
                [:text-run " Hello, world"] "!"]

               ;; not in the middle of the word
               "T~~hi~~s"
               [[:text-run "T"] "~" "~" [:text-run "hi"] "~" "~" [:text-run "s"]]

               ;; no spaces inside
               "Ain't ~~ working ~~"
               [[:text-run "Ain't "] "~" "~" [:text-run " working "] "~" "~"]))

  (t/testing "links"
    (parses-to sut/inline-parser->ast

               "[link text](/some/url)"
               [[:link {:text   "link text"
                        :target "/some/url"}]]

               ;; 3 sorts of link title
               "[link text](/some/url \"title\")"
               [[:link {:text   "link text"
                        :target "/some/url"
                        :title  "title"}]]

               "[link text](/some/url 'title')"
               [[:link {:text   "link text"
                        :target "/some/url"
                        :title  "title"}]]

               "[link text](/some/url (title))"
               [[:link {:text   "link text"
                        :target "/some/url"
                        :title  "title"}]]

               ;; link in an emphasis
               "this **[link](/example) is bold**"
               [[:text-run "this "]
                [:strong-emphasis
                 [:link {:text   "link"
                         :target "/example"}]
                 [:text-run " is bold"]]]

               ;; but no emphasis in a link
               "[*em*](/link)"
               [[:link {:text   "*em*"
                        :target "/link"}]]))

  (t/testing "images"
    (parses-to sut/inline-parser->ast

               "![link text](/some/url)"
               [[:image {:alt "link text"
                         :src "/some/url"}]]

               ;; 3 sorts of link title
               "![link text](/some/url \"title\")"
               [[:image {:alt   "link text"
                         :src   "/some/url"
                         :title "title"}]]

               "![link text](/some/url 'title')"
               [[:image {:alt   "link text"
                         :src   "/some/url"
                         :title "title"}]]

               "![link text](/some/url (title))"
               [[:image {:alt   "link text"
                         :src   "/some/url"
                         :title "title"}]]

               ;; link in an emphasis
               "this **![link](/example) is bold**"
               [[:text-run "this "]
                [:strong-emphasis
                 [:image {:alt "link"
                          :src "/example"}]
                 [:text-run " is bold"]]]

               ;; but no emphasis in a link
               "![*em*](/link)"
               [[:image {:alt "*em*"
                         :src "/link"}]]))

  (t/testing "autolinks"
    (parses-to sut/inline-parser->ast

               "<http://example.com>"
               [[:link {:text   "http://example.com"
                        :target "http://example.com"}]]

               ;; no white space in autolinks
               "<http://example.com and>"
               ["<" [:text-run "http://example.com and>"]]

               ;; emails are recognized
               "<root@example.com>"
               [[:link {:text "root@example.com"
                        :target "mailto:root@example.com"}]]))

  (t/testing "block references (Athens extension)"
    (parses-to sut/inline-parser->ast

               ;; just a block-ref
               "((block-id))"
               [[:block-ref "block-id"]]

               ;; in a middle of text-run
               "Text with ((block-id)) a block"
               [[:text-run "Text with "]
                [:block-ref "block-id"]
                [:text-run " a block"]]))

  (t/testing "hard line breaks"
    (parses-to sut/inline-parser->ast

               ;; hard line break can be only at the end of a line
               "abc  \ndef"
               [[:text-run "abc"]
                [:hard-line-break]
                [:text-run "def"]])))
