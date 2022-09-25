import React, { useMemo } from 'react'
import { Link, Text } from '@chakra-ui/react'

function MarkdownLink(title, url) {
  return { title, url }
}

/**
 * Ugly but working utility function that splits conent into multiple
 * parts which are used to render text or a markdown link
 * @param content
 * @return {*[]} an array of raw string values or an object representing a markdown link
 */
function splitContent(content) {
  const re =
    /\[((?:[^\[\]\\]|\\.)+)\]\((https?:\/\/(?:[-A-Z0-9+&@#\/%=~_|\[\]](?= *\))|[-A-Z0-9+&@#\/%?=~_|\[\]!:,.;](?! *\))|\([-A-Z0-9+&@#\/%?=~_|\[\]!:,.;(]*\))+) *\)/gi
  let match
  const out = []
  let caret = re.lastIndex
  while ((match = re.exec(content)) != null) {
    out.push(content.substring(caret, match.index))
    out.push(MarkdownLink(match[1], match[2]))
    caret = re.lastIndex
  }
  const rest = content.substring(caret, content.length)
  if (rest) out.push(rest)
  return out
}

export function MarkdownContent({ value, ...restProps }) {
  const parts = useMemo(() => splitContent(value), [value])
  return (
    <Text noOfLines={2} {...restProps}>
      {parts.map((part, ind) => {
        return part.url ? (
          <Link href={part.url} isExternal key={ind} color="teal.500">
            {part.title}
          </Link>
        ) : (
          <span key={ind}>{part}</span>
        )
      })}
    </Text>
  )
}
