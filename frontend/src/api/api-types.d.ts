declare type ApiNewTask = {
  title: string
  content: string
  deadline: string
  budget: number
  roomId: string
}

declare type ApiTask = {
  id: number
  title: string
  content: string
  deadline: string
  budget: number
  status: string
  author: string
  authorUserId: string
  assignee: string | null
  assigneeUserId: string | null
}

declare type ApiWallet = {
  id: string
  coinsAmount: number
  memberId: string
}
