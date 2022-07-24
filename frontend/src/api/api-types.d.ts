interface ApiNewTask {
  title: string
  content: string
  deadline: string
  budget: number
  roomId: string
}

interface ApiTask {
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

interface ApiWallet {
  id: string
  coinsAmount: number
  memberId: string
}

interface ApiMember {
  id: string
  name: string
}
